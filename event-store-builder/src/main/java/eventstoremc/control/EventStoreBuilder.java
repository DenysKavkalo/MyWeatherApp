package eventstoremc.control;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import predictionprovidermc.model.Weather;
import predictionprovidermc.control.InstantTypeAdapter;
import javax.jms.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class EventStoreBuilder {
    private final String topicName;
    private final String eventStoreDirectory;

    public EventStoreBuilder(String topicName, String eventStoreDirectory) {
        this.topicName = topicName;
        this.eventStoreDirectory = eventStoreDirectory;
    }

    public void start() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        String jsonEvent = textMessage.getText();

                        storeEvent(jsonEvent);
                    } catch (JMSException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeEvent(String jsonEvent) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();

        Weather weatherEvent = gson.fromJson(jsonEvent, Weather.class);

        String ss = weatherEvent.ss();
        Instant eventTimestamp = weatherEvent.ts();
        LocalDate eventDate = eventTimestamp.atZone(ZoneId.systemDefault()).toLocalDate();
        String formattedDate = eventDate.toString().replace("-", "");

        String eventStorePath = Paths.get(eventStoreDirectory, topicName, ss, formattedDate + ".events").toString();

        createDirectories(eventStorePath);

        try (FileWriter fileWriter = new FileWriter(eventStorePath, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            bufferedWriter.write(jsonEvent.replaceAll("\\n|\\r", "") + "\n");
        }
    }

    private void createDirectories(String path) {
        Path directoryPath = Paths.get(path).getParent();

        if (directoryPath != null) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
