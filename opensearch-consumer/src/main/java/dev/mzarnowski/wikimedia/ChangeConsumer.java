package dev.mzarnowski.wikimedia;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChangeConsumer {
    public static final String BOOTSTRAP_SERVER = "localhost:9092";
    public static final String TOPIC = "wikimedia-changes";
    public static final Logger LOGGER = LoggerFactory.getLogger(ChangeConsumer.class);
    public static final String WIKIMEDIA_CHANGES_INDEX = "wikimedia-changes";

    public static void main(String[] args) throws IOException {
        try (var client = openSearch(); var consumer = kafkaConsumer()) {
            createOpenSearchIndex(client);

            consumer.subscribe(List.of(TOPIC));

            var closed = new AtomicBoolean();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> closed.set(true)));
            while (!closed.get()) {
                var records = consumer.poll(Duration.ofSeconds(1));
                var request = new BulkRequest();
                for (var record : records) {
                    var indexRequest = openSearchIndexRequest(record);
                    request.add(indexRequest);
                }

                if (request.numberOfActions() > 0) {
                    var response = client.bulk(request, RequestOptions.DEFAULT);
                    LOGGER.info("Inserted {} records", response.getItems().length);
                    consumer.commitSync();
                    LOGGER.info("Committed offsets");
                }
            }
            LOGGER.info("Shutting down");
        }
    }

    private static void createOpenSearchIndex(RestHighLevelClient client) throws IOException {
        var exists = client.indices().exists(new GetIndexRequest(WIKIMEDIA_CHANGES_INDEX), RequestOptions.DEFAULT);
        if (exists) {
            LOGGER.info("Index exists!");
        } else {
            var request = new CreateIndexRequest(WIKIMEDIA_CHANGES_INDEX);
            var response = client.indices().create(request, RequestOptions.DEFAULT);
            LOGGER.info("Index created!");
        }
    }

    private static IndexRequest openSearchIndexRequest(ConsumerRecord<String, String> record) {
        var object = JsonParser.parseString(record.value()).getAsJsonObject();
        var meta = object.get("meta").getAsJsonObject();
        var id = meta.get("id").getAsString();

        return new IndexRequest(WIKIMEDIA_CHANGES_INDEX)
                .id(id)
                .source(record.value(), XContentType.JSON);
    }

    public static RestHighLevelClient openSearch() {
        var builder = RestClient.builder(new HttpHost("localhost", 9200));
        return new RestHighLevelClient(builder);
    }

    public static KafkaConsumer<String, String> kafkaConsumer() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "wikimedia-opensearch-sink");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return new KafkaConsumer<>(properties);
    }
}
