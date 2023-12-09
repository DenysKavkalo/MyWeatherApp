package predictionprovidermc.control;

import predictionprovidermc.model.Location;
import predictionprovidermc.model.Weather;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final ArrayList<Location> locations;
    private final WeatherProvider weatherProvider;
    private final WeatherStorage weatherStorage;
    private WeatherTask weatherTask;
    private long period;

    public WeatherController(WeatherProvider weatherProvider, WeatherStorage weatherStorage, String locationsFileLocation) {
        this.locations = loadLocationsFromFile(locationsFileLocation);
        this.weatherProvider = weatherProvider;
        this.weatherStorage = weatherStorage;
    }

    public static ArrayList<Location> loadLocationsFromFile(String filePath) {
        ArrayList<Location> locationList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    String name = parts[0];
                    double latitude = Double.parseDouble(parts[1]);
                    double longitude = Double.parseDouble(parts[2]);
                    Location location = new Location(name, latitude, longitude);
                    locationList.add(location);
                } else {
                    System.err.println("La línea no tiene suficientes partes: " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return locationList;
    }

    public void configure(int numDays, int hoursPeriodicity) {
        long hoursInMillis = (long) hoursPeriodicity * 60 * 60 * 1000;

        weatherTask = new WeatherTask(numDays);
        period = hoursInMillis;
    }

    public void execute() {
        Timer timer = new Timer(true);
        timer.schedule(weatherTask, 0, period);
    }

    private class WeatherTask extends TimerTask {
        private final int numDays;

        public WeatherTask(int numDays) {
            this.numDays = numDays;
        }

        @Override
        public void run() {
            Instant now = Instant.now();
            LocalTime noon = LocalTime.of(12, 0);

            for (Location location : locations) {
                Instant mediodia = now.truncatedTo(ChronoUnit.DAYS).plus(noon.getHour(), ChronoUnit.HOURS);

                int numDaysToAdd = (now.toEpochMilli() < mediodia.toEpochMilli()) ? 0 : 1;

                for (int i = 0; i <= numDays; i++) {
                    Instant targetInstant = mediodia.plus(i + numDaysToAdd, ChronoUnit.DAYS);

                    Weather weather = weatherProvider.get(location, targetInstant);

                    if (weather != null) {
                        weatherStorage.publish(weather);
                    }
                }
            }
        }
    }
}
