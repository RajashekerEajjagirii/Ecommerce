package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.dto.OrderResponse;
import com.raj.ecommerce.security.UserInfoDetailsService;
import com.raj.ecommerce.service.OrderService;
import com.raj.ecommerce.service.UserService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private UserInfoDetailsService userInfoDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(Principal principal) throws RazorpayException {
        User user=userService.findByEmail(principal.getName());
        return new ResponseEntity<>(orderService.placeOrder(user), HttpStatus.CREATED);
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<?> paymentWebhook(@RequestBody String payload, @RequestHeader("X-Signature") String sign){
        /*verify signature,parse event,
         * call orderservice.handlePaymentSuccess/Failure
         */
        return ResponseEntity.ok().build();
    }
}
