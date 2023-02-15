package dev.mzarnowski.shopping.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "product.offers.aggregation.quota=1")
public class ProductOffersEndpointTest {
        @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;

    @Test
    public void appends_new_offer() {
        // given
        var product = UUID.randomUUID().toString();
        var request = RequestEntity.post("http://localhost:{port}/product/{product}/offers", port, product)
                .header("Content-Type", "application/json")
                .body("1.1");
        // when
        var response = rest.exchange(request, Void.class);

        // then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void cannot_close_aggregation_without_reaching_quota() {
        // given
        var product = UUID.randomUUID().toString();
        var request = RequestEntity.put("http://localhost:{port}/product/{product}/offers/close", port, product).build();
        // when
        var response = rest.exchange(request, Void.class);

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void can_repeatedly_close_aggregation_after_reaching_quota() {
        // given
        var product = UUID.randomUUID().toString();
        var postRequest = RequestEntity.post("http://localhost:{port}/product/{product}/offers", port, product)
                .header("Content-Type", "application/json")
                .body("1.1");
        rest.exchange(postRequest, Void.class);

        // then
        var request = RequestEntity.put("http://localhost:{port}/product/{product}/offers/close", port, product).build();

        assertEquals(OK, rest.exchange(request, Void.class).getStatusCode());
        assertEquals(OK, rest.exchange(request, Void.class).getStatusCode());
    }


    @Test
    public void does_not_provide_aggregation_without_offers() {
        // given
        var product = UUID.randomUUID().toString();
        var request = RequestEntity.get("http://localhost:{port}/product/{product}/offers/aggregation", port, product)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        // when
        var response = rest.exchange(request, Void.class);

        // then
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void provide_aggregation_after_an_offer_was_posted() {
        // given
        var product = UUID.randomUUID().toString();

        var price = new BigDecimal("1.1");
        var postRequest = RequestEntity.post("http://localhost:{port}/product/{product}/offers", port, product)
                .header("Content-Type", "application/json")
                .body(price.toString());
        rest.exchange(postRequest, Void.class);

        // when
        var request = RequestEntity.get("http://localhost:{port}/product/{product}/offers/aggregation", port, product).accept(MediaType.APPLICATION_JSON).build();
        var response = rest.exchange(request, ProductOffers.Aggregation.class);

        // then
        var expectedBody = new ProductOffers.Aggregation(1, price, price, price.setScale(2, HALF_EVEN));
        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }
}