package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByActiveTrue(Pageable pageable);
}
