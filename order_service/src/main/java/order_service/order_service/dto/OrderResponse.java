package order_service.order_service.dto;

import order_service.order_service.model.CustomerOrder;
import order_service.order_service.model.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
