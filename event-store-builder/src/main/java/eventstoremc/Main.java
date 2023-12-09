package eventstoremc;

import eventstoremc.control.EventStoreBuilder;

public class Main {
    public static void main(String[] args) {
        String topicName = args[0];
        String eventStoreDirectory = args[1];

        EventStoreBuilder eventStoreBuilder = new EventStoreBuilder(topicName, eventStoreDirectory);
        eventStoreBuilder.start();
    }
}
