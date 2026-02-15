package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.Cart;
import com.raj.ecommerce.domain.CartItem;
import com.raj.ecommerce.domain.Product;
import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.dto.CartItemResponse;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.CartItemRepository;
import com.raj.ecommerce.repo.CartRepository;
import com.raj.ecommerce.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;
    @Autowired
    private CartItemRepository cartItemRepo;
    @Autowired
    private ProductRepository productRepo;

    public Cart getOrCreateCart(User user){
       return cartRepo.findByUserId(user.getId()).orElseGet(()->{
           Cart cart=new Cart();
           cart.setUser(user);
           return cartRepo.save(cart);
       });
    }

    public Set<CartItemResponse> getUserCart(User user){
        Cart cart=getOrCreateCart(user);
        List<CartItem> cartList= cartItemRepo.findAllBycartId(cart.getId()).orElseThrow(
                ()->new RecordNotFoundException("You don't have items,Cart was empty!"));
        return cartList.stream()
                .map(item->{
                   return CartItemResponse.builder()
                            .id(item.getId())
                            .productName(item.getProduct().getName())
                            .qty(item.getQty())
                            .priceSnapshot(item.getPriceSnapshot())
                            .build();
                }).collect(Collectors.toSet());

    }

    @Transactional
    public CartItem addItem(User user,Long productId,int qty){
        Cart cart=getOrCreateCart(user);
        Product product=productRepo.findById(productId).orElseThrow();
        CartItem item=new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQty(qty);
        item.setPriceSnapshot(product.getPrice());

        return cartItemRepo.save(item);
    }
}
