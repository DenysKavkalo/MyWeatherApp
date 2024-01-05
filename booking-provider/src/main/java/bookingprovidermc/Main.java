package bookingprovidermc;

import bookingprovidermc.control.*;

public class Main {
    public static void main(String[] args) {
        String hotelsLocation = args[0];
        String topicName = args[1];

        HotelProvider hotelProvider = new XoteloHotelProvider();
        HotelStorage hotelStorage = new HotelPublisher(topicName);

        HotelController controller = new HotelController(hotelProvider, hotelStorage, hotelsLocation);
        controller.configure(5, 6);
        controller.execute();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
