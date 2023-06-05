package dev.mzarnowski.wikimedia;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChangesProducer {
    public static final String BOOTSTRAP_SERVER = "localhost:9092";
    public static final String TOPIC = "wikimedia-changes";
    public static final URI URL = URI.create("https://stream.wikimedia.org/v2/stream/recentchange");

    public static final Logger LOGGER = LoggerFactory.getLogger(ChangesProducer.class);

    public static void main(String[] args) throws InterruptedException {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);

        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "50");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        var handler = new WikimediaChangeHandler(TOPIC, new KafkaProducer<>(properties));
        var builder = new BackgroundEventSource.Builder(handler, new EventSource.Builder(URL));

        try (var source = builder.build()){
            source.start();

            var closed = new AtomicBoolean();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> closed.set(true)));
            while (!closed.get()) Thread.sleep(Duration.ofSeconds(1));
            LOGGER.info("Shutting down");
        }
    }
}
