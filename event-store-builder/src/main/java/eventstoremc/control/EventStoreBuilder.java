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

    
}
