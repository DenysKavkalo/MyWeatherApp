package predictionprovidermc.control;

import predictionprovidermc.model.Weather;

public interface WeatherStorage {
    void publish(Weather weather);
}