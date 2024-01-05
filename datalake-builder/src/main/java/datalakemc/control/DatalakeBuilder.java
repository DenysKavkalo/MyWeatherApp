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
    import java.time.LocalDate;
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

        private MessageConsumer getConsumer() throws JMSException {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            connection.setClientID(clientId);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            return session.createDurableSubscriber(topic, subscriptionName);
        }

        private void storeEvent(String jsonEvent) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();

            JsonElement rootElement = gson.fromJson(jsonEvent, JsonElement.class);

            if (rootElement.isJsonObject()) {
                JsonObject jsonObject = rootElement.getAsJsonObject();

                String ss = jsonObject.has("ss") ? jsonObject.get("ss").getAsString() : null;

                Instant eventTimestamp = jsonObject.has("ts") ?
                        Instant.ofEpochMilli(jsonObject.get("ts").getAsLong()) : null;

                String formattedDate = null;
                if (eventTimestamp != null) {
                    LocalDate eventDate = eventTimestamp.atZone(ZoneId.systemDefault()).toLocalDate();
                    formattedDate = eventDate.toString().replace("-", "");
                }

                String eventStorePath = Paths.get(directory0, directory1,
                        topicName, ss, formattedDate + ".events").toString();
                createDirectories(eventStorePath);

                try (FileWriter fileWriter = new FileWriter(eventStorePath, true);
                     BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    bufferedWriter.write(jsonEvent.replaceAll("[\\n\\r]", "") + "\n");
                }
            } else {
                throw new RuntimeException("El JSON no es un objeto válido");
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
