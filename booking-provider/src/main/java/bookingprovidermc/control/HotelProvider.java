package bookingprovidermc.control;

import bookingprovidermc.model.Booking;
import bookingprovidermc.model.Hotel;

public interface HotelProvider {
    Booking get(Hotel hotel, String string, String checkOutString);
}
