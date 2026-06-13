package order_service.order_service.dto;

import order_service.order_service.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productSku,
        String productName,
        BigDecimal productPrice,
        Integer quantity,
        BigDecimal subtotal
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductSku(),
                item.getProductName(),
                item.getProductPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
