package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Long> {
   Optional<Shipment>  findByTrackingNumber(String trackingNumber);

    Optional<Shipment> findByOrderId(Long id);
}
