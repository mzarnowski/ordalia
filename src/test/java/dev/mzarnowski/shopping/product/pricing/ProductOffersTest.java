package dev.mzarnowski.shopping.product.pricing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static dev.mzarnowski.shopping.product.pricing.ProductOffers.*;
class ProductOffersTest {
    private static final ProductCode PRODUCT_CODE = new ProductCode("foo");

    @Test
    public void open_aggregation_can_be_closed(){
        // given an open aggregation
        var aggregation = new ProductOffers(PRODUCT_CODE);

        // then it can be closed
        var events = aggregation.close();
        assertEquals(List.of(new AggregationClosed(PRODUCT_CODE)), events);
    }

    @Test
    public void closed_aggregation_cannot_be_closed_again(){
        // given a closed aggregation
        var aggregation = new ProductOffers(PRODUCT_CODE);
        aggregation.close();

        // then it can be closed
        assertEquals(List.of(), aggregation.close());
    }
}