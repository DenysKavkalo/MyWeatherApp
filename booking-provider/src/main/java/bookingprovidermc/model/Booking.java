package bookingprovidermc.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

public record Booking(Hotel hotel, String checkIn, String checkOut, ArrayList<Rate> rates, Instant ts, String ss) {
}
