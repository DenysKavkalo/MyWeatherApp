package businessmvc;

import businessmvc.control.BusinessController;
import businessmvc.control.DataSubscriber;
import businessmvc.view.UserInteraction;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private static CountDownLatch latch = new CountDownLatch(2);

    public static void main(String[] args) {
        BusinessController businessController = new BusinessController();
        DataSubscriber subscriber = new DataSubscriber(businessController, args[0], args[1]);

        while (true) {
            executorService.submit(() -> {
                subscriber.startWeatherConsumer();
                latch.countDown();
            });

            executorService.submit(() -> {
                subscriber.startBookingConsumer();
                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            latch = new CountDownLatch(2);

            businessController.processAndStoreData();

            UserInteraction userInteraction = new UserInteraction();
            userInteraction.getUserInput();
            businessController.generateResponses(userInteraction.getCheckIn(), userInteraction.getCheckOut());
            userInteraction.printResponses(businessController.getResponses());
            businessController.clearResponses();
        }
    }
}
