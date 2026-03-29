package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins ="http://localhost:63342")
public class PaymentController {

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(@RequestBody Order order) throws StripeException {
        List<SessionCreateParams.PaymentMethodType> paymentTypes=new ArrayList<>();
        paymentTypes.add(SessionCreateParams.PaymentMethodType.ALIPAY);
        paymentTypes.add(SessionCreateParams.PaymentMethodType.AMAZON_PAY);
//        paymentTypes.add(SessionCreateParams.PaymentMethodType.PAYPAL);
        paymentTypes.add(SessionCreateParams.PaymentMethodType.CARD);

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllPaymentMethodType(paymentTypes)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/payment/success")
                .setCancelUrl("http://localhost:8080/api/payment/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(Long.valueOf(String.valueOf(order.getTotalAmount())))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(String.valueOf(order.getId()))
                                                                .build())
                                                .build())
                                .build())
                .build();


        Session session = Session.create(params);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("sessionId", session.getId());
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/success")
    public String getSuccess(){
        return "payment successful";
    }

    @GetMapping("/cancel")
    public String cancel(){
        return "payment canceled";
    }
}

