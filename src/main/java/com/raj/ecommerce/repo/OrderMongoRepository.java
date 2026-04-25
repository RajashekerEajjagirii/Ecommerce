package com.raj.ecommerce.repo;

import com.raj.ecommerce.domain.mongo.MongoOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderMongoRepository extends MongoRepository<MongoOrder, String> {

    Optional<MongoOrder> findByOrderId(Long id);
}
