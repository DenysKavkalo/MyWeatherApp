package control;

import model.Location;
import model.Weather;

import java.time.Instant;

public class OpenWeatherProvider {
    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={apiKey}&units=metric";
    private final String apiKey;

    public OpenWeatherProvider(String apiKeyLocation) {

    }

    @Override
    public Weather get(Location location, Instant instant) {

    }
}
