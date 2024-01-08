package predictionprovidermc;

import predictionprovidermc.control.*;

public class Main {
    public static void main(String[] args) {
        WeatherProvider weatherProvider = new OpenWeatherProvider(args[0]);
        WeatherStorage weatherStorage = new WeatherPublisher(args[2]);

        WeatherController controller = new WeatherController(weatherProvider, weatherStorage, args[1]);
        controller.configure(5, 6);
        controller.execute();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}