package com.raj.ecommerce.repo;

import com.raj.ecommerce.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<List<CartItem>> findAllByCartId(Long id);

    List<CartItem> findByCartId(Long id);


    Optional<CartItem> findByProductId(Long productId);
}
