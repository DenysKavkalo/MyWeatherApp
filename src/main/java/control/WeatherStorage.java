package control;

import model.Weather;

public interface WeatherStorage {
    void store(Weather weather);
}