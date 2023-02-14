package dev.mzarnowski.shopping.product.pricing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductOffersTest {
    @Test
    public void initially_an_aggregation_is_open(){
        // given a new aggregation
        var aggregation = new ProductOffers();

        // then it is open
        assertTrue(aggregation.isOpen());
    }

    @Test
    public void aggregation_can_be_closed(){
        // given an open aggregation
        var aggregation = new ProductOffers();

        // then it can be closed
        assertTrue(aggregation.close());
    }
}