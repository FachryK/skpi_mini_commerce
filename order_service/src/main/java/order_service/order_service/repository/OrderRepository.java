package order_service.order_service.repository;

import order_service.order_service.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByCustomerEmailIgnoreCase(String customerEmail);
}
