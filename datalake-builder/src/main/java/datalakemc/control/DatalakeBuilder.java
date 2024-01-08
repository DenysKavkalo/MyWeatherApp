package datalakemc.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import predictionprovidermc.control.InstantTypeAdapter;
import javax.jms.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;

public class DatalakeBuilder {
    private final String directory0;
    private final String directory1;
    private final String topicName;
    private final String clientId;
    private final String subscriptionName;

    public DatalakeBuilder(String directory0, String directory1, String topicName, String clientId,
                           String subscriptionName) {
        this.directory0 = directory0;
        this.directory1 = directory1;
        this.topicName = topicName;
        this.clientId = clientId;
        this.subscriptionName = subscriptionName;
    }

    public void start() {
        try {
            MessageConsumer consumer = getConsumer();

            consumer.setMessageListener(this::processTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MessageConsumer getConsumer() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        Connection connection = createConnection(connectionFactory);
        return createMessageConsumer(connection);
    }

    private Connection createConnection(ConnectionFactory connectionFactory) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(clientId);
        connection.start();
        return connection;
    }

    private MessageConsumer createMessageConsumer(Connection connection) throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        return session.createDurableSubscriber(topic, subscriptionName);
    }

    private void processTextMessage(Message message) {
        if (message instanceof TextMessage) {
            processText((TextMessage) message);
        }
    }

    private void processText(TextMessage textMessage) {
        try {
            String jsonEvent = textMessage.getText();
            storeEvent(jsonEvent);
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        }
    }

    private void storeEvent(String jsonEvent) throws IOException {
        Gson gson = createGsonWithInstantAdapter();
        JsonElement rootElement = gson.fromJson(jsonEvent, JsonElement.class);

        if (rootElement.isJsonObject()) {
            processJsonObject(jsonEvent, rootElement.getAsJsonObject());
        } else {
            throw new RuntimeException("The JSON object is not valid.");
        }
    }

    private Gson createGsonWithInstantAdapter() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
    }

    private void processJsonObject(String jsonEvent, JsonObject jsonObject) throws IOException {
        String ss = jsonObject.has("ss") ? jsonObject.get("ss").getAsString() : null;

        Instant eventTimestamp = jsonObject.has("ts") ?
                Instant.ofEpochMilli(jsonObject.get("ts").getAsLong()) : null;

        String formattedDate = getFormattedDate(eventTimestamp);

        String eventStorePath = buildEventStorePath(ss, formattedDate);
        createDirectories(eventStorePath);

        appendEventToFile(jsonEvent, eventStorePath);
    }

    private String getFormattedDate(Instant eventTimestamp) {
        return (eventTimestamp != null) ?
                eventTimestamp.atZone(ZoneId.systemDefault()).toLocalDate().toString().replace("-", "") :
                null;
    }

    private String buildEventStorePath(String ss, String formattedDate) {
        return Paths.get(directory0, directory1, topicName, ss, formattedDate + ".events").toString();
    }

    private void appendEventToFile(String jsonEvent, String eventStorePath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(eventStorePath, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(jsonEvent.replaceAll("[\\n\\r]", "") + "\n");
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
