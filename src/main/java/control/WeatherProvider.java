package control;

import model.Location;
import model.Weather;

import java.time.Instant;

public interface WeatherProvider {
    Weather get(Location location, Instant instant);
}