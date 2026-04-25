package com.raj.ecommerce.service;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.domain.*;
import com.raj.ecommerce.domain.mongo.MongoOrder;
import com.raj.ecommerce.domain.mongo.PaymentInfo;
import com.raj.ecommerce.domain.mongo.ProductInfo;
import com.raj.ecommerce.domain.mongo.ShipmentInfo;
import com.raj.ecommerce.dto.OrderResponse;
import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.PaymentResponse;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.exception.ServerDownException;
import com.raj.ecommerce.repo.*;
import com.raj.ecommerce.util.EmailBodyBuildTemplate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
    private MailService mailService;
    @Autowired
    private EmailBodyBuildTemplate emailTemplate;

    @Autowired
    private OrderMongoRepository orderMongoRepository;

    @Transactional
    public OrderResponse placeOrder(User user) {
        try {

            //fetch the cart details
            Cart cart = cartRepo.findByUserId(user.getId())
                    .orElseThrow(() -> new RecordNotFoundException("Cart was Empty!"));
            List<CartItem> items = cartItemRepo.findByCartId(cart.getId());
            if (items.isEmpty())
                throw new RecordNotFoundException("Cart was Empty!");

            /*Reserve the stock first*/
            for (CartItem item : items) {
                inventoryService.reserveStock(item.getProduct().getId(), item.getQty());
            }

            // Create order
            Order order = new Order();
            order.setUser(user);
            // create order instance for mongo collection
            MongoOrder mongoOrder=new MongoOrder();
            mongoOrder.setUserId(user.getId());
            //calculating total amount of items
            BigDecimal totalAmount = items.stream()
                    .map(item -> item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQty())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(totalAmount);
            order.setStatus(Constants.ORDER_STATUS_CREATED);
            order = orderRepo.save(order);
            mongoOrder.setOrderId(order.getId());
            orderMongoRepository.save(mongoOrder);
            int itemsCount = 0;
            List<ProductInfo> productInfoList=new ArrayList<>();
            // preparing order items
            for (CartItem ci : items) {
                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(ci.getProduct());
                oi.setQty(ci.getQty());
                itemsCount += ci.getQty();
                oi.setPrice(ci.getPriceSnapshot());
                // preparing Mongo
                ProductInfo productInfo=new ProductInfo();
                productInfo.setName(ci.getProduct().getName());
                productInfo.setProductId(ci.getProduct().getId());
                productInfo.setQuantity(ci.getQty());
                productInfo.setPrice(ci.getProduct().getPrice());
                productInfoList.add(productInfo);
                orderItemRepo.save(oi);
            }
            order = orderRepo.save(order);
            mongoOrder.setProducts(productInfoList);
            // Clear Cart
            cartItemRepo.deleteAll(items);

            // preparing payment request
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setName(user.getUsername());
            paymentRequest.setCurrency("INR");
            paymentRequest.setQuantity(itemsCount);
            paymentRequest.setAmount(totalAmount);
            paymentRequest.setReceipt("txn" + System.currentTimeMillis());
            //Initiate payment
             return processPaymentAndShipment(paymentRequest,order,mongoOrder);
        } catch (Exception e) {
            throw new ServerDownException("Exception occurred while placing order due to: "+e);
        }
    }

    public OrderResponse processPaymentAndShipment(PaymentRequest paymentRequest, Order order, MongoOrder mongoOrder) throws Exception {
        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
        String message = "";
        if (paymentResponse != null) {

            // saving payment details to db
            handlePaymentSuccess(order.getId(), paymentResponse,mongoOrder);

            //Initiating Shipment
            Shipment shipment=shipmentService.createShipment(order);
            //preparing shipmentInfo for mongodb
            ShipmentInfo shipmentInfo=new ShipmentInfo();
            shipmentInfo.setShipmentId(shipment.getId());
            shipmentInfo.setTrackingNumber(shipment.getTrackingNumber());
            shipmentInfo.setCarrier(shipment.getCarrier());
            shipmentInfo.setStatus(shipment.getStatus());
            mongoOrder.setShipment(shipmentInfo);

            order.setShipment(shipment);
            order.setStatus(Constants.ORDER_STATUS_SHIPPED);
            orderRepo.save(order);

            //saving to mongoDb
//            mongoOrder.setOrderId(order.getId());
            mongoOrder.setPrice(order.getTotalAmount());
            orderMongoRepository.save(mongoOrder);

            message = "Your order was placed successfully";
        } else {
            // Payment Failure scenario
            handlePaymentFailure(order.getId());
            message = "Your order was not placed!";
        }
        return toOrderResponse(order, message);
    }

    @Transactional
    public void handlePaymentSuccess(Long orderId, PaymentResponse paymentResponse, MongoOrder mongoOrder){
        Order order=orderRepo.findById(orderId).orElseThrow();
        order.setStatus(Constants.ORDER_STATUS_PAID);
        orderRepo.save(order);
        Optional<Payment> exists=paymentRepo.findByOrderId(orderId);
        if(exists.isEmpty()) {
            Payment paymentDetails = new Payment();
            paymentDetails.setOrder(order);
            paymentDetails.setGatewayTxnId(paymentResponse.getGatewayTxnId());
            paymentDetails.setStatus(paymentResponse.getStatus());
            paymentDetails.setAmount(order.getTotalAmount());
            paymentDetails.setCreatedAt(new Date().toInstant());
            // preparing info for mongo
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPaymentId(paymentResponse.getGatewayTxnId());
            paymentInfo.setMethod("Card");
            paymentInfo.setStatus(paymentResponse.getStatus());
            paymentInfo.setAmount(order.getTotalAmount());
            mongoOrder.setPayment(paymentInfo);

            paymentRepo.save(paymentDetails);
        }
    }

    @Transactional
    public void handlePaymentFailure(Long orderId){
        Order order=orderRepo.findById(orderId).orElseThrow(()->new RecordNotFoundException("order was not found!"));
        order.setStatus(Constants.ORDER_STATUS_CREATED);
        orderRepo.save(order);
        // release stock
        List<OrderItem> items=orderItemRepo.findByOrderId(order.getId());
        for(OrderItem item:items){
            inventoryService.releaseStock(item.getProduct().getId(),item.getQty());
        }
        String msg= "Hello "+order.getUser().getUsername()+"\n your order payment was failed having rupees of "+order.getTotalAmount()+". \n can you please try again later. \nThanks,\n Team Raj eCommerce";
        mailService.sendEmail(order.getUser().getEmail(),"Order Payment failure Information",
                emailTemplate.orderFailureTemplate(order.getUser().getUsername(),order.getTotalAmount()));
        throw new ServerDownException("Exception occurred while performing payment");
    }

    private OrderResponse toOrderResponse(Order order, String message) {
        String msg= "Hello "+order.getUser().getUsername()+"\n  "+message+" with payment of Rupees "+order.getTotalAmount()+" INR \n   you can find the shipment details by following tracking number: "+
                order.getShipment().getTrackingNumber()+". \nThanks,\n Team Raj eCommerce";
        mailService.sendHtmlEmail(order.getUser().getEmail(),"Order Placed Information",
                emailTemplate.orderSuccessTemplate(order.getUser().getUsername(),order.getTotalAmount(),order.getShipment().getTrackingNumber()));
        return OrderResponse.builder()
                .id(order.getId())
                .message(message)
                .status(order.getStatus())
                .total(order.getTotalAmount())
                .shipmentTrackingNumber(order.getShipment().getTrackingNumber())
                .build();
    }

    public String deleteAllOrders() {
        orderRepo.deleteAll();
        return "all orders deleted!";
    }
}
