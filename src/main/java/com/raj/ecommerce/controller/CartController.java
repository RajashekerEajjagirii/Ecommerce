package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.dto.CartItemRequest;
import com.raj.ecommerce.security.UserInfoDetailsService;
import com.raj.ecommerce.service.CartService;
import com.raj.ecommerce.util.DomainConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(
            value = "CartItemResponse",
            key = "#root.args[1].name",
            condition = "#root.args[1] != null && #root.args[1].name != null"
    )
    public ResponseEntity<String> addItem(@RequestBody @Valid CartItemRequest request, Principal principal){
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user=userInfoDetailsService.findByEmail(principal.getName());
        // CartItem item=cartService.addItem(user, request.getProductId(), request.getQty());
        //domainConverter.cartItemToCartItemResponseDto(item);
        return new ResponseEntity<>(cartService.addItem(user, request.getProductId(), request.getQty()), HttpStatus.CREATED);
    }

    @GetMapping
    // Cache per authenticated user (principal email). Guard against anonymous/null principals.
    @Cacheable(
            value = "CartItemResponse",
            key = "#root.args[0].name",
            condition = "#root.args[0] != null && #root.args[0].name != null",
            sync = true
    )
    public ResponseEntity<?> getCart(Principal principal){
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user=userInfoDetailsService.findByEmail(principal.getName());
//        Cart cart=cartService.getOrCreateCart(user);
        return new ResponseEntity<>(cartService.getUserCart(user),HttpStatus.OK);

    }

    @DeleteMapping("/{Id}")
    @CacheEvict(
            value = "CartItemResponse",
            key = "#root.args[1].name",
            condition = "#root.args[1] != null && #root.args[1].name != null"
    )
    public ResponseEntity<String> deleteCartItem(@PathVariable Long Id, Principal principal){
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(cartService.removeItemById(Id),HttpStatus.OK);
    }
}
