package dev.mzarnowski.shopping.product.pricing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ProductOffersConfiguration {
    @Bean
    ProductOfferRepository linearDiscountPolicy(@Value("${product.offers.aggregation.quota}") int quota) {
        return new ProductOfferRepository(quota);
    }
}
