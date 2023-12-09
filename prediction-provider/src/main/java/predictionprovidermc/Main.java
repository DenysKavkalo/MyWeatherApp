package predictionprovidermc;

import predictionprovidermc.control.*;

public class Main {
    public static void main(String[] args) {
        System.out.print("Welcome to the WeatherApp!\n");
        String apiKeyLocation = args[0];
        String locationsFileLocation = args[1];
        String topicName = args[2];

        WeatherProvider weatherProvider = new OpenWeatherProvider(apiKeyLocation);
        WeatherStorage weatherStorage = new WeatherPublisher(topicName);

        WeatherController controller = new WeatherController(weatherProvider, weatherStorage, locationsFileLocation);
        controller.configure(5, 6);
        controller.execute();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}