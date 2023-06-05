package dev.mzarnowski.wikimedia;

import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikimediaChangeHandler implements BackgroundEventHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangeHandler.class);
    // TODO event handler doesn't care about the topic
    //  wrap producer and topic in a thin-wrapper
    private final String topic;
    private final KafkaProducer<String, String> producer;

    public WikimediaChangeHandler(String topic, KafkaProducer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void onOpen() {
        LOGGER.debug("Stream open");
    }

    @Override
    public void onClosed() {
        producer.close();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) {
        var record = new ProducerRecord<String, String>(topic, messageEvent.getData());
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                LOGGER.info("Produced message");
            } else {
                LOGGER.error("Failed to produce message", exception);
            }
        });
    }

    @Override
    public void onComment(String comment) {
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.error("Error ", t);
    }
}
