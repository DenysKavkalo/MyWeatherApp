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
                    WeatherBooking newWeatherBooking = createWeatherBookingObject(weather, booking, smallestRate);

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
                .map(rate -> rate.rate() + rate.tax())
                .min(Float::compareTo);

        return smallestRate.orElse(0.0f);
    }

    private WeatherBooking createWeatherBookingObject(Weather weather, Booking booking, Float smallestRate) {
        return new WeatherBooking(
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
    }

    public synchronized void generateResponses(String checkIn, String checkOut) {
        List<WeatherBooking> filteredBookings = filterBookings(checkIn, checkOut);

        Map<String, Map<String, List<WeatherBooking>>> groupedBookings = groupBookingsByHotelAndIsland(filteredBookings);

        processGroupedBookings(checkIn, checkOut, groupedBookings);
    }

    private List<WeatherBooking> filterBookings(String checkIn, String checkOut) {
        return dataMart.stream()
                .filter(booking -> booking.checkIn().compareTo(checkIn) >= 0
                        && booking.checkOut().compareTo(checkOut) <= 0)
                .collect(Collectors.toList());
    }

    private Map<String, Map<String,
            List<WeatherBooking>>> groupBookingsByHotelAndIsland(List<WeatherBooking> filteredBookings) {
        return filteredBookings.stream()
                .collect(Collectors.groupingBy(WeatherBooking::hotel,
                        Collectors.groupingBy(WeatherBooking::island)));
    }

    private void processGroupedBookings(String checkIn, String checkOut, Map<String, Map<String,
            List<WeatherBooking>>> groupedBookings) {
        for (Map.Entry<String, Map<String, List<WeatherBooking>>> entry : groupedBookings.entrySet()) {
            String hotel = entry.getKey();
            Map<String, List<WeatherBooking>> islandBookings = entry.getValue();
            processIslandBookings(checkIn, checkOut, hotel, islandBookings);
        }
    }

    private void processIslandBookings(String checkIn, String checkOut, String hotel, Map<String,
            List<WeatherBooking>> islandBookings) {
        islandBookings.forEach((island, bookings) -> {
            boolean allDaysCovered = areAllDaysCovered(checkIn, checkOut, bookings);

            if (allDaysCovered) {
                ResponseToUserPetition response = generateResponse(checkIn, checkOut, hotel, island, bookings);
                responses.add(response);
            }
        });
    }

    private boolean areAllDaysCovered(String checkIn, String checkOut, List<WeatherBooking> bookings) {
        LocalDate startDate = LocalDate.parse(checkIn);
        LocalDate endDate = LocalDate.parse(checkOut);

        while (startDate.isBefore(endDate)) {
            String currentDay = startDate.toString();
            if (bookings.stream().noneMatch(booking -> booking.checkIn().equals(currentDay))) {
                return false;
            }
            startDate = startDate.plusDays(1);
        }
        return true;
    }

    private ResponseToUserPetition generateResponse(String checkIn, String checkOut, String hotel, String island,
                                                    List<WeatherBooking> bookings) {
        float totalRate = calculateTotalRate(bookings);
        float meanTemp = calculateMeanValue(bookings, WeatherBooking::temp);
        float meanRain = calculateMeanValue(bookings, WeatherBooking::rain);
        float meanHumidity = calculateMeanValue(bookings, WeatherBooking::humidity);
        float meanClouds = calculateMeanValue(bookings, WeatherBooking::clouds);
        float meanWindSpeed = calculateMeanValue(bookings, WeatherBooking::windSpeed);

        return new ResponseToUserPetition(hotel, island, checkIn, checkOut,
                totalRate, meanTemp, meanRain, meanHumidity, meanClouds, meanWindSpeed);
    }

    private float calculateTotalRate(List<WeatherBooking> bookings) {
        return (float) bookings.stream()
                .mapToDouble(WeatherBooking::smallestRate)
                .sum();
    }

    private float calculateMeanValue(List<WeatherBooking> bookings, ValueExtractor<WeatherBooking> extractor) {
        int bookingsCount = bookings.size();
        float sum = (float) bookings.stream()
                .mapToDouble(extractor::extract)
                .sum();
        return sum / bookingsCount;
    }

    private interface ValueExtractor<T> {
        float extract(T t);
    }
}
