package order_service.order_service.controller;

import jakarta.validation.Valid;
import order_service.order_service.dto.CreateOrderRequest;
import order_service.order_service.dto.OrderResponse;
import order_service.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        return orderService.create(request, authentication);
    }

    @GetMapping
    public List<OrderResponse> findAll(Authentication authentication) {
        return orderService.findAll(authentication);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id, Authentication authentication) {
        return orderService.findById(id, authentication);
    }

    @PatchMapping("/{id}/pay")
    public OrderResponse pay(@PathVariable Long id, Authentication authentication) {
        return orderService.pay(id, authentication);
    }

    @PatchMapping({"/{id}/cancel", "/{id}.cancel"})
    public OrderResponse cancel(@PathVariable Long id, Authentication authentication) {
        return orderService.cancel(id, authentication);
    }
}
