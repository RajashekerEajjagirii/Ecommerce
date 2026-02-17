package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.Cart;
import com.raj.ecommerce.domain.CartItem;
import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.dto.CartItemRequest;
import com.raj.ecommerce.dto.CartItemResponse;
import com.raj.ecommerce.security.UserInfoDetailsService;
import com.raj.ecommerce.service.CartService;
import com.raj.ecommerce.util.DomainConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private UserInfoDetailsService userInfoDetailsService;
    @Autowired
    private CartService cartService;
    @Autowired
    private DomainConverter domainConverter;

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(@RequestBody @Valid CartItemRequest request, Principal principal){
        User user=userInfoDetailsService.findByEmail(principal.getName());
        CartItem item=cartService.addItem(user, request.getProductId(), request.getQty());
//        domainConverter.cartItemToCartItemResponseDto(item);
        return new ResponseEntity<>(domainConverter.cartItemToCartItemResponseDto(item), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getCart(Principal principal){
        User user=userInfoDetailsService.findByEmail(principal.getName());
//        Cart cart=cartService.getOrCreateCart(user);
        return new ResponseEntity<>(cartService.getUserCart(user),HttpStatus.FOUND);

    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long Id){
        return new ResponseEntity<>(cartService.removeItemById(Id),HttpStatus.OK);
    }
}
