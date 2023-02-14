package dev.mzarnowski.shopping.product.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductOffersTest {
    @Test
    public void initially_an_aggregation_is_open(){
        // given a new aggregation
        var aggregation = new ProductOffers();

        // then it is open
        assertTrue(aggregation.isOpen());
    }
}