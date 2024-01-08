package bookingprovidermc;

import bookingprovidermc.control.*;

public class Main {
    public static void main(String[] args) {

        HotelProvider hotelProvider = new XoteloHotelProvider();
        HotelStorage hotelStorage = new HotelPublisher(args[1]);

        HotelController controller = new HotelController(hotelProvider, hotelStorage, args[0]);
        controller.configure(5, 6);
        controller.execute();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
