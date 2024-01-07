package bookingprovidermc.control;

import bookingprovidermc.model.Booking;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import predictionprovidermc.control.InstantTypeAdapter;
import javax.jms.*;
import java.time.Instant;

public class HotelPublisher implements HotelStorage{
    private final String topicName;

    public HotelPublisher(String topicName) {this.topicName = topicName;}

    public void publish(Booking booking){
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .setPrettyPrinting()
                    .create();

            String jsonHotel = gson.toJson(booking);
            System.out.println("Booking JSON: \n" + jsonHotel);
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(topic);
            TextMessage message = session.createTextMessage(jsonHotel);
            producer.send(message);
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
