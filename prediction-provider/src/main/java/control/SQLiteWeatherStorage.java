package control;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SQLiteWeatherStorage implements WeatherStorage {
    private static final String JDBC_URL = "jdbc:sqlite:weather_database.db";

    public SQLiteWeatherStorage(String locationsFileLocation) {
        initializeDatabase(WeatherController.loadLocationsFromFile(locationsFileLocation));
    }

    private void initializeDatabase(ArrayList<Location> locationsFileLocation) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            for (Location location : locationsFileLocation) {

                String tableName = location.name().toLowerCase();
                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "latitude DOUBLE," +
                        "longitude DOUBLE," +
                        "instant TIMESTAMP UNIQUE," +
                        "temperature FLOAT," +
                        "rain FLOAT," +
                        "humidity INTEGER," +
                        "clouds INTEGER," +
                        "wind_speed FLOAT" +
                        ")";
                statement.executeUpdate(createTableSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(Weather weather) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT OR REPLACE INTO " + weather.location().name().toLowerCase() +
                             " (latitude, longitude, instant, temperature, rain, humidity, clouds, wind_speed) " +
                             "VALUES (?, ?, ?, ROUND(?, 2), ROUND(?, 2), ?, ?, ROUND(?, 2))"
             )) {

            preparedStatement.setDouble(1, weather.location().latitude());
            preparedStatement.setDouble(2, weather.location().longitude());

            Timestamp timestamp = Timestamp.from(weather.timeStamp());
            LocalDateTime localDateTime = timestamp.toLocalDateTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = localDateTime.format(formatter);

            preparedStatement.setString(3, formattedDateTime);

            preparedStatement.setFloat(4, weather.temp());
            preparedStatement.setFloat(5, weather.rain());
            preparedStatement.setInt(6, weather.humidity());
            preparedStatement.setInt(7, weather.clouds());
            preparedStatement.setFloat(8, weather.windSpeed());

            preparedStatement.executeUpdate();
            System.out.println("Data stored successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}