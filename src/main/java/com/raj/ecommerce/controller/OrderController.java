package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.security.UserInfoDetailsService;
import com.raj.ecommerce.service.OrderService;
import com.raj.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public ResponseEntity<?> placeOrder(Principal principal){
        User user=userService.findByEmail(principal.getName());
        return new ResponseEntity<>(orderService.placeOrder(user), HttpStatus.CREATED);
    }
}
