package predictionprovidermc.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import predictionprovidermc.model.Weather;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.time.Instant;

public class JMSWeatherStorage implements WeatherStorage {

    private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String TOPIC_NAME = "prediction.Weather";

    @Override
    public void publish(Weather weather) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(topic);

            String jsonWeather = convertWeatherToJson(weather);

            TextMessage message = session.createTextMessage(jsonWeather);

            producer.send(message);

            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private String convertWeatherToJson(Weather weather) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
        return gson.toJson(weather);
    }
}
