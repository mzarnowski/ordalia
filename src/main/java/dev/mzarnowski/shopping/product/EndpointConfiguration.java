package dev.mzarnowski.shopping.product;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class EndpointConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        var module = new SimpleModule("price");
        module.addDeserializer(Price.class, new BigDecimalCustomDeserializer());

        return new ObjectMapper().registerModule(module);
    }

    static class BigDecimalCustomDeserializer extends JsonDeserializer<Price> {
        @Override
        public Price deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            var value = parser.getNumberValueExact();
            if (value instanceof BigDecimal decimal) {
                return Price.of(decimal);
            }

            return Price.of(new BigDecimal(value.toString()));
        }
    }
}
