package predictionprovidermc.model;

import java.time.Instant;

public record Weather(Float temp, Float rain, Integer humidity, Integer clouds, Float windSpeed, Location location,
                      Instant predictionTime, Instant ts, String ss) {
}