package businessmvc;

import businessmvc.control.BusinessController;
import businessmvc.control.DataSubscriber;
import businessmvc.view.UserInteraction;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        String weatherTopic = args[0];
        String hotelTopic = args[1];

        BusinessController businessController = new BusinessController();
        CountDownLatch latch = new CountDownLatch(2);
        DataSubscriber subscriber = new DataSubscriber(businessController, weatherTopic, hotelTopic, latch);

        subscriber.startWeatherConsumer();
        subscriber.startBookingConsumer();

        System.out.println("Presiona Enter para finalizar los consumidores.");
        new Scanner(System.in).nextLine();

        subscriber.terminateConsumers();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UserInteraction userInteraction = new UserInteraction();
        userInteraction.getUserInput();
        //businessController.processEvents();
        businessController.getResponses(userInteraction.getCheckIn(),userInteraction.getCheckOut());
    }
}