package businessmvc.control;

import businessmvc.model.ResponseToUserPetition;
import businessmvc.model.WeatherBooking;
import predictionprovidermc.model.Weather;
import bookingprovidermc.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BusinessController {
    private final List<Weather> weatherList = new ArrayList<>();
    private final List<Booking> bookingList = new ArrayList<>();
    private final List<WeatherBooking> dataMart = new ArrayList<>();
    private final List<ResponseToUserPetition> responses = new ArrayList<>();

    public void addWeather(Weather weather) {
        weatherList.add(weather);
    }

    public void addBooking(Booking booking) {
        bookingList.add(booking);
    }

    public void processEvents(Weather weather, Booking booking) {
        return;
    }

    public List<ResponseToUserPetition> getResponses(String checkIn, String checkOut) {
        return responses;
    }
}
