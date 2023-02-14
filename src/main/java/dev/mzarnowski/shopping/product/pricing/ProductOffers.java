package dev.mzarnowski.shopping.product.pricing;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductOffers {
    private final ProductCode productCode;
    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public ProductOffers(ProductCode productCode) {
        this.productCode = productCode;
    }

    public List<Event> close() {
        if (isOpen.compareAndSet(true, false)) {
            return List.of(new AggregationClosed(productCode));
        }
        return List.of();
    }

    public List<Event> append(ProductCode productCode, Price price) {
        return List.of(new OfferAppended(productCode, price));
    }

    public sealed interface Event {}

    public record AggregationClosed(ProductCode productCode) implements Event {}

    public record OfferAppended(ProductCode productCode, Price price) implements Event {}
}
