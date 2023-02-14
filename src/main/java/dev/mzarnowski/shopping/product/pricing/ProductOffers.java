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

    public sealed interface Event {}

    public record AggregationClosed(ProductCode productCode) implements Event {}

}
