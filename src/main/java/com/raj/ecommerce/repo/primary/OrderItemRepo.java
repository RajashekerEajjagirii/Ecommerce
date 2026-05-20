package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByOrderId(Long id);

    List<OrderItem> findAllByOrderId(Long id);
}
