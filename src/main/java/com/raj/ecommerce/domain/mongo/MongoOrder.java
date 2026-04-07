package com.raj.ecommerce.domain.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "orders")
@Data
public class MongoOrder {

    @Id
    private String id;
    private long orderId;
    private long userId;
    private List<ProductInfo> products;
    private PaymentInfo payment;
    private ShipmentInfo shipment;
    private String orderStatus;
    private BigDecimal price;
}
