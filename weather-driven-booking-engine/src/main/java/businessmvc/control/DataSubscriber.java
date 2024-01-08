package businessmvc.control;

import bookingprovidermc.model.Booking;
import bookingprovidermc.model.Hotel;
import bookingprovidermc.model.Rate;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import predictionprovidermc.model.Location;
import predictionprovidermc.model.Weather;
import javax.jms.*;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class DataSubscriber {
    private final BusinessController businessController;
    private final CountDownLatch latch;
    private volatile boolean terminate = false;
    private final Object lock = new Object();
    private boolean paused = false;
    private static final String BASE_URL = "tcp://localhost:61616?wireFormat.maxInactivityDuration=0";
    private TopicConnection weatherConnection;
    private TopicConnection bookingConnection;

    public DataSubscriber(BusinessController businessController, String weatherTopicName, String bookingTopicName) {
        this.businessController = businessController;
        this.latch = new CountDownLatch(2);
        initializeWeatherSubscriber(weatherTopicName);
        initializeBookingSubscriber(bookingTopicName);
    }

    private void waitForUnpause() {
        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeWeatherSubscriber(String weatherTopicName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BASE_URL);
            weatherConnection = (TopicConnection) connectionFactory.createConnection();
            TopicSession weatherSession = weatherConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic weatherTopic = weatherSession.createTopic(weatherTopicName);
            TopicSubscriber weatherSubscriber = weatherSession.createSubscriber(weatherTopic);

            weatherSubscriber.setMessageListener(message -> {
                waitForUnpause();
                if (message instanceof TextMessage) {
                    processWeatherMessage((TextMessage) message);
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void processWeatherMessage(TextMessage textMessage) {
        try {
            String json = textMessage.getText();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            if (jsonNode.has("temp") && jsonNode.has("rain")) {
                processWeatherData(jsonNode);
            }
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void processWeatherData(JsonNode jsonNode) {
        JsonNode locationNode = jsonNode.get("location");
        Location location = createLocationFromNode(locationNode);

        float temp = jsonNode.get("temp").floatValue();
        float rain = jsonNode.get("rain").floatValue();
        int humidity = jsonNode.get("humidity").intValue();
        int clouds = jsonNode.get("clouds").intValue();
        float windSpeed = jsonNode.get("windSpeed").floatValue();
        Instant predictionTime = Instant.ofEpochMilli(jsonNode.get("predictionTime").asLong());
        Instant ts = Instant.ofEpochMilli(jsonNode.get("ts").asLong());
        String ss = jsonNode.get("ss").asText();

        Weather weather = new Weather(temp, rain, humidity, clouds, windSpeed,
                location, predictionTime, ts, ss);
        businessController.addWeather(weather);
    }

    private Location createLocationFromNode(JsonNode locationNode) {
        String locationName = locationNode.get("name").asText();
        double latitude = locationNode.get("latitude").asDouble();
        double longitude = locationNode.get("longitude").asDouble();
        return new Location(locationName, latitude, longitude);
    }

    private void initializeBookingSubscriber(String bookingTopicName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BASE_URL);
            bookingConnection = (TopicConnection) connectionFactory.createConnection();
            TopicSession bookingSession = bookingConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic bookingTopic = bookingSession.createTopic(bookingTopicName);
            TopicSubscriber bookingSubscriber = bookingSession.createSubscriber(bookingTopic);

            bookingSubscriber.setMessageListener(message -> {
                waitForUnpause();
                if (message instanceof TextMessage) {
                    processBookingMessage((TextMessage) message);
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void processBookingMessage(TextMessage textMessage) {
        try {
            String json = textMessage.getText();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            if (jsonNode.has("hotel") && jsonNode.has("checkIn") && jsonNode.has("checkOut")) {
                processBookingData(jsonNode);
            }
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void processBookingData(JsonNode jsonNode) {
        JsonNode hotelNode = jsonNode.get("hotel");
        Hotel hotel = createHotelFromNode(hotelNode);

        String checkIn = jsonNode.get("checkIn").asText();
        String checkOut = jsonNode.get("checkOut").asText();

        ArrayList<Rate> rates = createRatesFromNode(jsonNode);
        Instant ts = Instant.ofEpochMilli(jsonNode.get("ts").asLong());
        String ss = jsonNode.get("ss").asText();

        Booking booking = new Booking(hotel, checkIn, checkOut, rates, ts, ss);
        businessController.addBooking(booking);
    }

    private Hotel createHotelFromNode(JsonNode hotelNode) {
        String hotelName = hotelNode.get("name").asText();
        String island = hotelNode.get("island").asText();
        String key = hotelNode.get("key").asText();
        return new Hotel(hotelName, island, key);
    }

    private ArrayList<Rate> createRatesFromNode(JsonNode jsonNode) {
        ArrayList<Rate> rates = new ArrayList<>();
        JsonNode ratesNode = jsonNode.get("rates");
        if (ratesNode.isArray()) {
            for (JsonNode rateNode : ratesNode) {
                String code = rateNode.get("code").asText();
                String webPage = rateNode.get("webPage").asText();
                Float rate = rateNode.get("rate").floatValue();
                Float tax = rateNode.get("tax").floatValue();
                Rate rateObj = new Rate(code, webPage, rate, tax);
                rates.add(rateObj);
            }
        }
        return rates;
    }

    public void startWeatherConsumer() {
        startConsumer(weatherConnection);
    }

    public void startBookingConsumer() {
        startConsumer(bookingConnection);
    }

    private void startConsumer(TopicConnection connection) {
        Thread consumerThread = new Thread(() -> {
            try {
                connection.start();
                while (!terminate) {
                }
            } catch (JMSException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        consumerThread.start();
    }

}
