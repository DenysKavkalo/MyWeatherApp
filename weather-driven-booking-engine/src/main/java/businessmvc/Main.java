package businessmvc;

import businessmvc.control.BusinessController;
import businessmvc.control.DataSubscriber;
import businessmvc.view.UserInteraction;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);


    public static void main(String[] args) {
        BusinessController businessController = new BusinessController();
        DataSubscriber subscriber = new DataSubscriber(businessController, args[0], args[1]);
        UserInteraction userInteraction = new UserInteraction();

        while (true) {

            executorService.submit(subscriber::startWeatherConsumer);

            executorService.submit(subscriber::startBookingConsumer);

            // Captura la entrada del usuario antes de iniciar los consumidores
            userInteraction.getUserInput();

            // Espera a que ambos consumidores terminen (si es necesario)
            // ...

            businessController.processAndStoreData();

            // Utiliza la entrada del usuario capturada antes de imprimir respuestas
            businessController.generateResponses(userInteraction.getCheckIn(), userInteraction.getCheckOut());
            userInteraction.printResponses(businessController.getResponses());
            businessController.clearResponses();
        }

    }
}
