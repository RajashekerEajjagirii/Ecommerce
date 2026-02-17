package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.Payment;
import com.raj.ecommerce.repo.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String razorpayId;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;

    private RazorpayClient razorpayClient;

    private PaymentRepository paymentRepository;

    @PostConstruct
    public void init() throws RazorpayException{
        this.razorpayClient=new RazorpayClient(razorpayId,razorpaySecret);
    }


    //Inject gateway client or adapter (Razorpay/PayU)
    @Transactional
    public boolean createPaymentForOrder(Order order) throws RazorpayException {
        //create payment request to gateway,return payment page/url
        JSONObject orderRequest=new JSONObject();
        orderRequest.put("amount",order.getTotalAmount());
        orderRequest.put("currency","INR");
        orderRequest.put("receipt","txn"+System.currentTimeMillis());
        com.razorpay.Order razorpayResponse=razorpayClient.orders.create(orderRequest);
        if(razorpayResponse ==null)
            throw  new RazorpayException("Payment server was down, try again!");
        // saving payment details to db
        Payment paymentInfo=new Payment();
        paymentInfo.setOrder(order);
        paymentInfo.setGatewayTxnId(razorpayResponse.get("id"));
        paymentInfo.setStatus(razorpayResponse.get("status"));
        paymentInfo.setAmount(order.getTotalAmount());
        paymentInfo.setCreatedAt(new Date().toInstant());
        paymentRepository.save(paymentInfo);
        return true;
    }

    public boolean verifyWebhookSignature(String payload,String signature){
        //verify using gateway secret
        return true;
    }
}
