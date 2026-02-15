package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.*;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.CartItemRepository;
import com.raj.ecommerce.repo.CartRepository;
import com.raj.ecommerce.repo.OrderItemRepo;
import com.raj.ecommerce.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepo;
    @Autowired
    private CartItemRepository cartItemRepo;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;

    @Transactional
    public Order placeOrder(User user) {
        //fetch the cart details
        Cart cart=cartRepo.findByUserId(user.getId())
                .orElseThrow(()->new RecordNotFoundException("Cart was Empty!"));
        List<CartItem> items=cartItemRepo.findByCartId(cart.getId());
        if(items.isEmpty())
            throw new RecordNotFoundException("Cart was Empty!");

        /*Reserve the stock first*/
        for(CartItem item:items){
           inventoryService.reserveStock(item.getProduct().getId(),item.getQty());
        }

        // Create order
        Order order=new Order();
        order.setUser(user);
        //calculating total amount of items
        BigDecimal totalAmount=items.stream()
                .map(item->item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQty())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING_PAYMENT");
        order=orderRepo.save(order);

        // preparing order items
        for(CartItem ci:items){
            OrderItem oi=new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQty(ci.getQty());
            oi.setPrice(ci.getPriceSnapshot());
            orderItemRepo.save(oi);
        }

        // Clear Cart
        cartItemRepo.deleteAll(items);

        //Initiate payment(returns payment url oor token)


        return order;
    }
}
