package com.raj.ecommerce.service;


import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.PaymentResponse;
import com.raj.ecommerce.repo.primary.PaymentRepository;
import com.raj.ecommerce.util.PaymentGateway;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("razorpayGateway")
public class RazorpayService implements PaymentGateway {

    @Value("${razorpay.key_id}")
    private String razorpayId;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;


    private final RazorpayClient razorpayClient;

    private PaymentRepository paymentRepository;

    public RazorpayService(@Value("${razorpay.key_id}")String razorpayId, @Value("${razorpay.key_secret}") String razorpaySecret) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayId,razorpaySecret);
    }


//    @PostConstruct
//    public void init() throws RazorpayException{
//        this.razorpayClient=new RazorpayClient(razorpayId,razorpaySecret);
//    }


    //Inject gateway client or adapter (Razorpay/PayU)
    @Transactional
    @Override
    public PaymentResponse createPaymentForOrder(PaymentRequest paymentRequest) throws RazorpayException {
        //create payment request to gateway,return payment page/url
        JSONObject orderRequest=new JSONObject();
        orderRequest.put("amount",paymentRequest.getAmount());
        orderRequest.put("currency",paymentRequest.getCurrency());
        orderRequest.put("receipt",paymentRequest.getReceipt());
        com.razorpay.Order razorpayResponse=razorpayClient.orders.create(orderRequest);
        if(razorpayResponse ==null)
            throw  new RazorpayException("Payment server was down, try again!");
        PaymentResponse paymentResponse=new PaymentResponse();
        paymentResponse.setGatewayTxnId(razorpayResponse.get("id"));
        paymentResponse.setStatus(razorpayResponse.get("status"));
//        // saving payment details to db
//        Payment paymentInfo=new Payment();
//        paymentInfo.setOrder(order);
//        paymentInfo.setGatewayTxnId(razorpayResponse.get("id"));
//        paymentInfo.setStatus(razorpayResponse.get("status"));
//        paymentInfo.setAmount(order.getTotalAmount());
//        paymentInfo.setCreatedAt(new Date().toInstant());
//        paymentRepository.save(paymentInfo);
        return paymentResponse;
    }

    public boolean verifyWebhookSignature(String payload,String signature){
        //verify using gateway secret
        return true;
    }
}
