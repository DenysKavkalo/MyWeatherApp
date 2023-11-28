package control;

import model.*;
import java.time.Instant;

public interface WeatherProvider {
    Weather get(Location location, Instant instant);
}