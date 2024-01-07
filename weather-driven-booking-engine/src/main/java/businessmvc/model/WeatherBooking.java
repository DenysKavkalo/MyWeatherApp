package businessmvc.model;

import bookingprovidermc.model.Rate;
import java.time.Instant;
import java.util.ArrayList;

public record WeatherBooking(String hotel, String island, String checkIn, String checkOut,
                             Float smallestRate, Float temp, Float rain, Integer humidity, Integer clouds,
                             Float windSpeed) {
}
