package order_service.order_service.repository;

import order_service.order_service.model.CustomerOrder;
import order_service.order_service.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    @Query("""
            SELECT o FROM CustomerOrder o
            WHERE (:customerEmail IS NULL OR LOWER(o.customerEmail) = LOWER(:customerEmail))
            AND (:status IS NULL OR o.status = :status)
            """)
    List<CustomerOrder> search(@Param("customerEmail") String customerEmail, @Param("status") OrderStatus status);
}
