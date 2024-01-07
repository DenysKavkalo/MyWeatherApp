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
    private boolean terminate = false;
    private TopicConnection weatherConnection;
    private TopicConnection bookingConnection;

    public DataSubscriber(BusinessController businessController, String weatherTopicName, String bookingTopicName, CountDownLatch latch) {
        this.businessController = businessController;
        this.latch = latch;
        initializeWeatherSubscriber(weatherTopicName);
        initializeBookingSubscriber(bookingTopicName);
    }

    private void initializeWeatherSubscriber(String weatherTopicName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            weatherConnection = (TopicConnection) connectionFactory.createConnection();
            TopicSession weatherSession = weatherConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic weatherTopic = weatherSession.createTopic(weatherTopicName);
            TopicSubscriber weatherSubscriber = weatherSession.createSubscriber(weatherTopic);

            weatherSubscriber.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        String json = textMessage.getText();

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(json);

                        if (jsonNode.has("temp") && jsonNode.has("rain")) {
                            JsonNode locationNode = jsonNode.get("location");

                            String locationName = locationNode.get("name").asText();
                            double latitude = locationNode.get("latitude").asDouble();
                            double longitude = locationNode.get("longitude").asDouble();

                            Location location = new Location(locationName, latitude, longitude);

                            float temp = jsonNode.get("temp").floatValue();
                            float rain = jsonNode.get("rain").floatValue();
                            int humidity = jsonNode.get("humidity").intValue();
                            int clouds = jsonNode.get("clouds").intValue();
                            float windSpeed = jsonNode.get("windSpeed").floatValue();
                            Instant predictionTime = Instant.ofEpochMilli(jsonNode.get("predictionTime").asLong());
                            Instant ts = Instant.ofEpochMilli(jsonNode.get("ts").asLong());
                            String ss = jsonNode.get("ss").asText();

                            Weather weather = new Weather(temp, rain, humidity, clouds, windSpeed, location, predictionTime, ts, ss);
                            businessController.addWeather(weather);
                        }
                    } catch (JMSException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void initializeBookingSubscriber(String bookingTopicName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            bookingConnection = (TopicConnection) connectionFactory.createConnection();
            TopicSession bookingSession = bookingConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic bookingTopic = bookingSession.createTopic(bookingTopicName);
            TopicSubscriber bookingSubscriber = bookingSession.createSubscriber(bookingTopic);

            bookingSubscriber.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        String json = textMessage.getText();

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(json);

                        if (jsonNode.has("hotel") && jsonNode.has("checkIn") && jsonNode.has("checkOut")) {
                            JsonNode hotelNode = jsonNode.get("hotel");

                            String hotelName = hotelNode.get("name").asText();
                            String island = hotelNode.get("island").asText();
                            String key = hotelNode.get("key").asText();

                            Hotel hotel = new Hotel(hotelName, island, key);

                            String checkIn = jsonNode.get("checkIn").asText();
                            String checkOut = jsonNode.get("checkOut").asText();

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

                            Instant ts = Instant.ofEpochMilli(jsonNode.get("ts").asLong());
                            String ss = jsonNode.get("ss").asText();

                            Booking booking = new Booking(hotel, checkIn, checkOut, rates, ts, ss);
                            businessController.addBooking(booking);
                        }
                    } catch (JMSException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // Método para esperar a que ambos consumidores terminen
    public void awaitTermination() throws InterruptedException {
        while (!terminate) {
            // Esperar activamente hasta que se reciba la señal de terminación
            Thread.sleep(100);
        }
    }

    // Método para enviar la señal de terminación a los consumidores
    public void terminateConsumers() {
        terminate = true;
    }

    // Método para iniciar el consumidor de Weather en un hilo separado
    public void startWeatherConsumer() {
        Thread weatherThread = new Thread(() -> {
            try {
                weatherConnection.start();

                // Esperar activamente hasta que se reciba la señal de terminación
                while (!terminate) {
                    // Continuar la ejecución del consumidor de Weather
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        weatherThread.start();
    }

    // Método para iniciar el consumidor de Booking en un hilo separado
    public void startBookingConsumer() {
        Thread bookingThread = new Thread(() -> {
            try {
                bookingConnection.start();

                // Esperar activamente hasta que se reciba la señal de terminación
                while (!terminate) {
                    // Continuar la ejecución del consumidor de Booking
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        bookingThread.start();
    }
}
