package zielu.playground.kafka;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EmbeddedKafkaBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKafkaBaseTest.class);

    @ClassRule
    public static final EmbeddedZookeeper zookeeper = new EmbeddedZookeeper(AvailablePortFinder.getNextAvailable(24000));

    @ClassRule
    public static final EmbeddedKafkaBroker kafkaBroker = new EmbeddedKafkaBroker(1, AvailablePortFinder.getNextAvailable(25000),
        zookeeper.getConnection());

    @BeforeClass
    public static void logKafkaConfig() {
        LOG.info("## Zookeeper: {}", zookeeper.getConnection());
        LOG.info("## Kafka brokers: {}", kafkaBroker.getBrokerList());
    }
}
