package dev.mzarnowski.shopping.product.pricing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static dev.mzarnowski.shopping.product.pricing.ProductOffers.*;
import static java.math.BigDecimal.ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductOffersTest {
    private static final ProductCode PRODUCT_CODE = new ProductCode("foo");
    private static final Price PRICE = Price.of(ONE);
    private static final int QUOTA = 0;

    @Test
    public void initially_offer_aggregation_is_empty() {
        // given product with no offers
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);

        // then there are no aggregated offers
        assertEquals(Optional.empty(), productOffers.getAggregation());
    }

    @Test
    public void updates_offer_statistics_only_while_open() {
        // given product with no offers
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);

        // when an offer is appended
        productOffers.append(PRODUCT_CODE, Price.of(BigDecimal.valueOf(1)));
        productOffers.append(PRODUCT_CODE, Price.of(BigDecimal.valueOf(2)));
        productOffers.append(PRODUCT_CODE, Price.of(BigDecimal.valueOf(3)));
        productOffers.append(PRODUCT_CODE, Price.of(BigDecimal.valueOf(4)));

        // then the aggregation is closed
        productOffers.close();

        // and another offer is tried
        productOffers.append(PRODUCT_CODE, Price.of(BigDecimal.valueOf(10000)));

        // then the price after closing is not included in the statistics
        var expected = new ProductOffers.Aggregation(4L, ONE, BigDecimal.valueOf(4), new BigDecimal("2.50"));
        assertEquals(Optional.of(expected), productOffers.getAggregation());
    }

    @Test
    public void open_aggregation_accepts_new_offers() {
        // given an open aggregation
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);

        // then new offer can be appended
        var events = productOffers.append(PRODUCT_CODE, PRICE);
        assertEquals(new OfferAppended(PRODUCT_CODE, PRICE), events);
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
            assertEquals(Optional.of(expectedEvent), events);

            productOffers.append(PRODUCT_CODE, PRICE);
        }

        var events = productOffers.close();
        assertEquals(Optional.of(new AggregationClosed(PRODUCT_CODE)), events);
    }

    @Test
    public void closed_aggregation_cannot_be_closed_again() {
        // given a closed aggregation
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);
        productOffers.close();

        // then it can be closed
        assertEquals(Optional.empty(), productOffers.close());
    }

    @Test
    public void closed_aggregation_does_not_accept_new_offers() {
        // given an open aggregation
        var productOffers = new ProductOffers(PRODUCT_CODE, QUOTA);
        productOffers.close();

        // then new offer can be appended
        var result = new FailedAppendingOffer(PRODUCT_CODE, PRICE, new AggregationIsClosed(PRODUCT_CODE));
        var events = productOffers.append(PRODUCT_CODE, PRICE);
        assertEquals(result, events);
    }
}