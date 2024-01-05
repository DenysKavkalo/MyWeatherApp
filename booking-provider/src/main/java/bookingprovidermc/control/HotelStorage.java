package bookingprovidermc.control;

import bookingprovidermc.model.Booking;

public interface HotelStorage {
    void publish(Booking booking);
}
