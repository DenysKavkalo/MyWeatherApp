package businessmvc;

import businessmvc.control.BusinessController;
import businessmvc.control.DataSubscriber;
import businessmvc.view.UserInteraction;

public class Main {
    public static void main(String[] args) {
        BusinessController businessController = new BusinessController();
        DataSubscriber subscriber = new DataSubscriber(businessController, args[0], args[1]);

        Thread subscriberThread = new Thread(() -> {
            subscriber.startWeatherConsumer();
            subscriber.startBookingConsumer();
        });
        subscriberThread.start();

        while (true) {
            businessController.processAndStoreData();

            UserInteraction userInteraction = new UserInteraction();
            userInteraction.getUserInput();
            businessController.generateResponses(userInteraction.getCheckIn(), userInteraction.getCheckOut());
            userInteraction.printResponses(businessController.getResponses());
            businessController.clearResponses();
        }
    }
}
