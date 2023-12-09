package eventstoremc.control;

import org.apache.activemq.ActiveMQConnectionFactory;
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
        Instant eventTimestamp = Instant.now();
        LocalDate eventDate = Instant.ofEpochMilli(eventTimestamp.toEpochMilli())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        String formattedDate = eventDate.toString().replace("-", "");

        String eventStorePath = Paths.get(eventStoreDirectory, topicName, formattedDate + ".events").toString();

        createDirectories(eventStorePath);

        try (FileWriter fileWriter = new FileWriter(eventStorePath, true)) {
            fileWriter.write(jsonEvent);
            fileWriter.write("\n");
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
