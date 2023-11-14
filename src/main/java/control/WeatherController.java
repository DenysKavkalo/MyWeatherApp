package control;

import model.Location;

import java.util.ArrayList;

public class WeatherController {
    private final ArrayList<Location> locations;
    private final WeatherProvider weatherProvider;
    private final WeatherStorage weatherStore;
    private long period;

    public WeatherController(WeatherProvider weatherProvider, WeatherStorage weatherStore, String locationsFileLocation) {
        this.weatherProvider = weatherProvider;
        this.weatherStore = weatherStore;
    }
}