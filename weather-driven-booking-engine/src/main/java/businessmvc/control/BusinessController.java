package businessmvc.control;

import bookingprovidermc.model.Rate;
import businessmvc.model.ResponseToUserPetition;
import businessmvc.model.WeatherBooking;
import predictionprovidermc.model.Weather;
import bookingprovidermc.model.Booking;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BusinessController {
    private final List<Weather> weatherList = new ArrayList<>();
    private final List<Booking> bookingList = new ArrayList<>();
    private final List<WeatherBooking> dataMart = new ArrayList<>();
    private final List<ResponseToUserPetition> responses = new ArrayList<>();

    public List<ResponseToUserPetition> getResponses() {
        return responses;
    }

    public void addWeather(Weather weather) {
        weatherList.add(weather);
    }

    public void addBooking(Booking booking) {
        bookingList.add(booking);
    }

    public synchronized void clearResponses() {
        responses.clear();
    }

    public synchronized void processAndStoreData() {
        for (Weather weather : weatherList) {
            for (Booking booking : bookingList) {
                if (areConditionsMet(weather, booking)) {
                    Float smallestRate = calculateSmallestRate(booking);

                    WeatherBooking newWeatherBooking = new WeatherBooking(
                            booking.hotel().name(),
                            booking.hotel().island(),
                            booking.checkIn(),
                            booking.checkOut(),
                            smallestRate,
                            weather.temp(),
                            weather.rain(),
                            weather.humidity(),
                            weather.clouds(),
                            weather.windSpeed()
                    );

                    if (dataMart.contains(newWeatherBooking)) {
                        dataMart.remove(newWeatherBooking);
                    }

                    dataMart.add(newWeatherBooking);
                }
            }
        }
    }

    private boolean areConditionsMet(Weather weather, Booking booking) {

        return weather.location().name().equals(booking.hotel().island())
                && areDatesEqual(weather.predictionTime(), booking.checkIn());
    }

    private boolean areDatesEqual(Instant predictionTime, String checkIn) {
        LocalDate predictionDate =
                Instant.ofEpochMilli(predictionTime.toEpochMilli()).atZone(ZoneOffset.UTC).toLocalDate();

        LocalDate checkInDate = LocalDate.parse(checkIn);

        return predictionDate.isEqual(checkInDate);
    }

    private Float calculateSmallestRate(Booking booking) {
        List<Rate> rates = booking.rates();

        Optional<Float> smallestRate = rates.stream()
                .map(rate -> rate.rate() + rate.rate() * rate.tax())
                .min(Float::compareTo);

        return smallestRate.orElse(0.0f);
    }

    public synchronized void generateResponses(String checkIn, String checkOut) {
        List<WeatherBooking> filteredBookings = dataMart.stream()
                .filter(booking -> booking.checkIn().compareTo(checkIn) >= 0
                        && booking.checkOut().compareTo(checkOut) <= 0)
                .collect(Collectors.toList());

        Map<String, Map<String, List<WeatherBooking>>> groupedBookings = filteredBookings.stream()
                .collect(Collectors.groupingBy(WeatherBooking::hotel,
                        Collectors.groupingBy(WeatherBooking::island)));

        for (Map.Entry<String, Map<String, List<WeatherBooking>>> entry : groupedBookings.entrySet()) {
            String hotel = entry.getKey();
            Map<String, List<WeatherBooking>> islandBookings = entry.getValue();
            islandBookings.forEach((island, bookings) -> {

                boolean allDaysCovered = true;
                LocalDate startDate = LocalDate.parse(checkIn);
                LocalDate endDate = LocalDate.parse(checkOut);

                while (startDate.isBefore(endDate)) {
                    String currentDay = startDate.toString();
                    if (bookings.stream().noneMatch(booking -> booking.checkIn().equals(currentDay))) {
                        allDaysCovered = false;
                        break;
                    }
                    startDate = startDate.plusDays(1);
                }

                if (allDaysCovered) {
                    float totalRate = 0.0f;
                    float meanTemp = 0.0f;
                    float meanRain = 0.0f;
                    float meanHumidity = 0.0f;
                    float meanClouds = 0.0f;
                    float meanWindSpeed = 0.0f;

                    for (WeatherBooking booking : bookings) {
                        totalRate += booking.smallestRate();
                        meanTemp += booking.temp();
                        meanRain += booking.rain();
                        meanHumidity += booking.humidity();
                        meanClouds += booking.clouds();
                        meanWindSpeed += booking.windSpeed();
                    }

                    int bookingsCount = bookings.size();
                    meanTemp /= bookingsCount;
                    meanRain /= bookingsCount;
                    meanHumidity /= bookingsCount;
                    meanClouds /= bookingsCount;
                    meanWindSpeed /= bookingsCount;

                    ResponseToUserPetition response = new ResponseToUserPetition(hotel, island, checkIn, checkOut,
                            totalRate, meanTemp, meanRain, meanHumidity, meanClouds, meanWindSpeed);
                    responses.add(response);
                }
            });
        }
    }
}
