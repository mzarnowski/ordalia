package dev.mzarnowski.shopping.product.pricing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static dev.mzarnowski.shopping.product.pricing.ProductOffers.*;

class ProductOffersTest {
    private static final ProductCode PRODUCT_CODE = new ProductCode("foo");
    private static final Price PRICE = Price.of(BigDecimal.ONE);
    private static final int QUOTA = 0;

    @Test
    public void initially_offer_aggregation_is_empty(){
        // given no product offers
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);

        // then there are no aggregated offers
        assertEquals(productOffers.getAggregation(), Optional.empty());
    }

    @Test
    public void open_aggregation_accepts_new_offers() {
        // given an open aggregation
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);

        // then new offer can be appended
        var events = productOffers.append(PRODUCT_CODE, PRICE);
        assertEquals(List.of(new OfferAppended(PRODUCT_CODE, PRICE)), events);
    }

    @Test
    public void open_aggregation_can_be_closed_only_after_reaching_quota() {
        // given an open aggregation with a quota
        var quota = 10;
        var productOffers = new ProductOffers(PRODUCT_CODE, quota);

        // then the aggregation can be closed only after reaching the quota
        for (int i = 0; i < quota; ++i) {
            var expectedEvent = new FailedClosingAggregation(PRODUCT_CODE, new QuotaNotReached(PRODUCT_CODE, quota - i));
            var events = productOffers.close();
            assertEquals(List.of(expectedEvent), events);

            productOffers.append(PRODUCT_CODE, PRICE);
        }

        var events = productOffers.close();
        assertEquals(List.of(new AggregationClosed(PRODUCT_CODE)), events);
    }

    @Test
    public void closed_aggregation_cannot_be_closed_again() {
        // given a closed aggregation
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);
        productOffers.close();

        // then it can be closed
        assertEquals(List.of(), productOffers.close());
    }

    @Test
    public void closed_aggregation_does_not_accept_new_offers() {
        // given an open aggregation
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);
        productOffers.close();

        // then new offer can be appended
        var expectedEvent = new FailedAppendingOffer(PRODUCT_CODE, PRICE, new AggregationIsClosed(PRODUCT_CODE));
        var events = productOffers.append(PRODUCT_CODE, PRICE);
        assertEquals(List.of(expectedEvent), events);
    }
}