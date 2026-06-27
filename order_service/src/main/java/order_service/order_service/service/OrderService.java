package order_service.order_service.service;

import order_service.order_service.catalog.CatalogClient;
import order_service.order_service.catalog.ProductResponse;
import order_service.order_service.catalog.ProductStatus;
import order_service.order_service.catalog.StockAdjustmentRequest;
import order_service.order_service.common.ApiException;
import order_service.order_service.dto.CreateOrderItemRequest;
import order_service.order_service.dto.CreateOrderRequest;
import order_service.order_service.dto.OrderResponse;
import order_service.order_service.model.CustomerOrder;
import order_service.order_service.model.OrderItem;
import order_service.order_service.model.OrderStatus;
import order_service.order_service.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, Authentication authentication) {
        assertEmailAuthorized(request.customerEmail(), authentication, "Customer email must match authenticated user");

        Map<Long, ProductResponse> products = request.items().stream()
                .map(CreateOrderItemRequest::productId)
                .distinct()
                .map(catalogClient::getProduct)
                .collect(Collectors.toMap(ProductResponse::id, Function.identity()));

        for (CreateOrderItemRequest item : request.items()) {
            ProductResponse product = products.get(item.productId());
            validateProductCanBeOrdered(product, item.quantity());
        }

        List<CreateOrderItemRequest> adjustedItems = new ArrayList<>();
        try {
            for (CreateOrderItemRequest item : request.items()) {
                catalogClient.adjustStock(item.productId(), new StockAdjustmentRequest(-item.quantity()));
                adjustedItems.add(item);
            }

            CustomerOrder order = new CustomerOrder(request.customerName().trim(), request.customerEmail().trim().toLowerCase(Locale.ROOT));
            for (CreateOrderItemRequest item : request.items()) {
                ProductResponse product = products.get(item.productId());
                order.addItem(new OrderItem(
                        product.id(),
                        product.sku(),
                        product.name(),
                        product.price(),
                        item.quantity()
                ));
            }

            return OrderResponse.from(orderRepository.save(order));
        } catch (RuntimeException exception) {
            restoreAdjustedStock(adjustedItems);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll(OrderStatus status, Authentication authentication) {
        String customerEmail = isAdmin(authentication) ? null : authentication.getName();
        return orderRepository.search(customerEmail, status).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id, Authentication authentication) {
        CustomerOrder order = getOrder(id);
        assertEmailAuthorized(order.getCustomerEmail(), authentication, "You can only access your own orders");
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse pay(Long id, Authentication authentication) {
        CustomerOrder order = getOrder(id);
        assertEmailAuthorized(order.getCustomerEmail(), authentication, "You can only access your own orders");
        assertPending(order);
        order.markPaid();
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancel(Long id, Authentication authentication) {
        CustomerOrder order = getOrder(id);
        assertEmailAuthorized(order.getCustomerEmail(), authentication, "You can only access your own orders");
        assertPending(order);
        for (OrderItem item : order.getItems()) {
            catalogClient.adjustStock(item.getProductId(), new StockAdjustmentRequest(item.getQuantity()));
        }
        order.markCancelled();
        return OrderResponse.from(order);
    }

    private void validateProductCanBeOrdered(ProductResponse product, int quantity) {
        if (product.status() != ProductStatus.ACTIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product " + product.id() + " is inactive");
        }
        if (product.stock() < quantity) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product " + product.id() + " has insufficient stock");
        }
    }

    private CustomerOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private void assertPending(CustomerOrder order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only PENDING orders can be paid or cancelled");
        }
    }

    private void assertEmailAuthorized(String email, Authentication authentication, String message) {
        if (!isAdmin(authentication) && !authentication.getName().equalsIgnoreCase(email)) {
            throw new ApiException(HttpStatus.FORBIDDEN, message);
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private void restoreAdjustedStock(List<CreateOrderItemRequest> adjustedItems) {
        for (CreateOrderItemRequest item : adjustedItems) {
            catalogClient.adjustStock(item.productId(), new StockAdjustmentRequest(item.quantity()));
        }
    }
}
