package model;

import java.time.Instant;

public record Weather(Float temp, Float rain, Integer humidity, Integer clouds, Float windSpeed, Location location,
                      Instant timeStamp) {
}