package com.raj.ecommerce.repo.primary;

import com.raj.ecommerce.domain.primary.ShipmentTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentTrackerRepo extends JpaRepository<ShipmentTracker,Long> {

    Optional<ShipmentTracker> findFirstByTrackingNumberOrderByEventTimeDesc(String trackingNumber);

    List<ShipmentTracker> findByTrackingNumber(String trackingNumber);
}
