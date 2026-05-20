package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<Address,Long> {
}
