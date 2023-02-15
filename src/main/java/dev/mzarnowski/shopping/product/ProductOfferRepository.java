package dev.mzarnowski.shopping.product;

import java.util.HashMap;
import java.util.Map;

public class ProductOfferRepository {
    private final int quota;
    private final Map<ProductCode, ProductOffers> offers = new HashMap<>();

    public ProductOfferRepository(int quota) {
        this.quota = quota;
    }

    public ProductOffers offersOf(ProductCode productCode){
        return offers.computeIfAbsent(productCode, it -> new ProductOffers(it, quota));
    }
}
