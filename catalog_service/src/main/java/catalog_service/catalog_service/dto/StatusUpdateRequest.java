package catalog_service.catalog_service.dto;

import catalog_service.catalog_service.model.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull ProductStatus status) {
}
