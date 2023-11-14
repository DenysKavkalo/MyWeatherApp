package control;


import model.Weather;

public class SQLiteWeatherStorage implements WeatherStorage {
    private static final String JDBC_URL = "jdbc:sqlite:weather_database.db";

    public SQLiteWeatherStorage(String locationsFileLocation) {

    }

    @Override
    public void store(Weather weather) {

    }
}