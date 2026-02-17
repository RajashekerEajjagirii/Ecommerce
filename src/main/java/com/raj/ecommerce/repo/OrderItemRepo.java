package com.raj.ecommerce.repo;

import com.raj.ecommerce.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByOrderId(Long id);
}
