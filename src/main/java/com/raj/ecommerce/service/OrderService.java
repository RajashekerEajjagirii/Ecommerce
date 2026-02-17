package com.raj.ecommerce.service;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.domain.*;
import com.raj.ecommerce.dto.OrderResponse;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.*;
import com.razorpay.RazorpayException;
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
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ShipmentRepository shipmentRepo;

    @Transactional
    public OrderResponse placeOrder(User user) throws RazorpayException {
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
        boolean  paymentStatus = paymentService.createPaymentForOrder(order);
        String message="";
        if(paymentStatus){
            //Handling Shipment
            Shipment shipment=shipmentService.createShipment(order);
            //saving shipment data to db
            shipmentRepo.save(shipment);
            order.setShipment(shipment);
            order.setStatus(Constants.ORDER_STATUS_SHIPPED);
            orderRepo.save(order);
            message="Your order was placed successfully!";
        }else {
            // Payment Failure scenario
            handlePaymentFailure(order.getId());
            message="Your order was not placed,try again!";
        }
        return toOrderResponse(order,message);
    }

    @Transactional
    public void handlePaymentSuccess(Long orderId,String gatewayTxnId){
        Order order=orderRepo.findById(orderId).orElseThrow();
        order.setStatus("PAID");
        orderRepo.save(order);
        Payment paymentInfo=new Payment();
        paymentInfo.setOrder(order);
        paymentInfo.setGatewayTxnId(gatewayTxnId);
        paymentInfo.setAmount(order.getTotalAmount());
        paymentInfo.setStatus("SUCCESS");
        paymentRepo.save(paymentInfo);
    }

    @Transactional
    public void handlePaymentFailure(Long orderId){
        Order order=orderRepo.findById(orderId).orElseThrow(()->new RecordNotFoundException("order was not found!"));
        order.setStatus("PAYMENT_FAILED");
        orderRepo.save(order);
        // release stock
        List<OrderItem> items=orderItemRepo.findByOrderId(order.getId());
        for(OrderItem item:items){
            inventoryService.releaseStock(item.getProduct().getId(),item.getQty());
        }
    }

    private OrderResponse toOrderResponse(Order order, String message) {

        return OrderResponse.builder()
                .id(order.getId())
                .message(message)
                .status(order.getStatus())
                .total(order.getTotalAmount())
                .shipmentTrackingNumber(order.getShipment().getTrackingNumber())
                .build();
    }
}
