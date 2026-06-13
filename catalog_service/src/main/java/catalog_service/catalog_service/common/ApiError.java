package catalog_service.catalog_service.common;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> details
) {
}
