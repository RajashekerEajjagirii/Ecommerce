package com.raj.ecommerce.repo;

import com.raj.ecommerce.domain.mongo.MongoOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<MongoOrder, String> {
}
