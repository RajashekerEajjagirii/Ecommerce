package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUserId(Long id);

    Optional<List<Cart>> findAllByUserId(Long id);
}
