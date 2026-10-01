package lab.product.infresct;

import lab.product.domain.Product;
import lab.product.domain.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(Long id, Long storeId, String name, BigDecimal price, Integer stock, ProductStatus status) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getStore().getId(), product.getName(),
                product.getPrice(), product.getStock(), product.getStatus());
    }
}
