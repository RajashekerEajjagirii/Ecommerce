package com.raj.ecommerce.dto;

import com.raj.ecommerce.domain.primary.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Order order;
    private String gatewayTxnId;
    private String status;
}
