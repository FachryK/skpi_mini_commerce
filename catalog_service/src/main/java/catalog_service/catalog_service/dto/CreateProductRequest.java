package catalog_service.catalog_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotNull @Min(0) Integer stock
) {
}
