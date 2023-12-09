package predictionprovidermc.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import predictionprovidermc.model.Weather;
import java.time.Instant;

public class WeatherPublisher implements WeatherStorage{
    private final String topicName;

    public WeatherPublisher(String topicName) {
        this.topicName = topicName;
    }

    public void publish(Weather weather) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .setPrettyPrinting()
                    .create();

            String jsonWeather = gson.toJson(weather);

            System.out.println("Weather JSON: \n" + jsonWeather);

            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);

            MessageProducer producer = session.createProducer(topic);

            TextMessage message = session.createTextMessage(jsonWeather);

            producer.send(message);

            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
