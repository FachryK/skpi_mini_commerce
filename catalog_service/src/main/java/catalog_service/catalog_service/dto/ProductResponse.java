package catalog_service.catalog_service.dto;

import catalog_service.catalog_service.model.Product;
import catalog_service.catalog_service.model.ProductStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
