package predictionprovidermc.control;

import predictionprovidermc.model.Location;
import predictionprovidermc.model.Weather;

import java.time.Instant;

public interface WeatherProvider {
    Weather get(Location location, Instant instant);
}