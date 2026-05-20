package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.primary.Cart;
import com.raj.ecommerce.domain.primary.CartItem;
import com.raj.ecommerce.domain.primary.Product;
import com.raj.ecommerce.domain.primary.User;
import com.raj.ecommerce.dto.CartItemResponse;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.exception.ServerDownException;
import com.raj.ecommerce.repo.primary.CartItemRepository;
import com.raj.ecommerce.repo.primary.CartRepository;
import com.raj.ecommerce.repo.primary.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private InventoryService inventoryService;

    public Cart getOrCreateCart(User user){
       return cartRepo.findByUserId(user.getId()).orElseGet(()->{
           Cart cart=new Cart();
           cart.setUser(user);
           return cartRepo.save(cart);
       });
    }

    public Set<CartItemResponse> getUserCart(User user){
        Cart cart=getOrCreateCart(user);
        List<CartItem> cartList= cartItemRepo.findAllByCartId(cart.getId()).orElseThrow(
                ()->new RecordNotFoundException("You don't have items,Cart was empty!"));
        return cartList.stream()
                .map(item->{
                   return CartItemResponse.builder()
                            .id(item.getId())
                            .productName(item.getProduct().getName())
                            .qty(item.getQty())
                            .priceSnapshot(item.getPriceSnapshot())
                           .isInStock(inventoryService.isStockAvailable(item.getProduct().getId(),item.getQty()))
                           .createdTs(item.getCreatedTs())
                            .build();
                }).collect(Collectors.toSet());

    }

    @Transactional
    public String addItem(User user,Long productId,int qty){
        try {
            Cart cart = getOrCreateCart(user);
            Product product = productRepo.findById(productId).orElseThrow();
            Optional<CartItem> existItem = cartItemRepo.findByProductId(productId);
            if (existItem.isPresent()) {
                qty += existItem.get().getQty();
                existItem.get().setQty(qty);
                existItem.get().setPriceSnapshot(product.getPrice().multiply(BigDecimal.valueOf(qty)));
                existItem.get().setInStock(inventoryService.isStockAvailable(product.getId(),qty));
                existItem.get().setUpdatedTs(LocalDateTime.now());
                cartItemRepo.save(existItem.get());
                return "Your requested quantity of products are added to the Cart";
            } else {
                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQty(qty);
                item.setPriceSnapshot(product.getPrice());
                item.setInStock(inventoryService.isStockAvailable(product.getId(),qty));
                item.setCreatedTs(LocalDateTime.now());
                cartItemRepo.save(item);
                return "Your product was added to the Cart";
            }
        } catch (Exception e) {
            throw new ServerDownException("Exception occurred while adding item to the Cart: "+e);
        }
    }

    public String removeItemById(Long Id) {
        cartItemRepo.findById(Id).orElseThrow(()->new RecordNotFoundException("Item was not found!"));
        cartItemRepo.deleteById(Id);
        return "Item was removed from the Cart successfully!";
    }
}
