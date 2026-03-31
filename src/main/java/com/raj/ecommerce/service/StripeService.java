package com.raj.ecommerce.service;

import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.PaymentResponse;
import com.raj.ecommerce.exception.ServerDownException;
import com.raj.ecommerce.util.PaymentGateway;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("stripeGateway")
@ConditionalOnProperty(name = "payment.provider", havingValue = "stripe")
public class StripeService implements PaymentGateway {

    public StripeService( @Value("${stripe.secret_key}") String secretKey){
         Stripe.apiKey=secretKey;
    }

    @Override
    @Transactional
    public PaymentResponse createPaymentForOrder(PaymentRequest paymentRequest) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount",paymentRequest.getAmount().longValue());
        params.put("currency", paymentRequest.getCurrency());
        //preparing payment types
        List<SessionCreateParams.PaymentMethodType> paymentTypes=new ArrayList<>();
//        paymentTypes.add(SessionCreateParams.PaymentMethodType.ALIPAY);
//        paymentTypes.add(SessionCreateParams.PaymentMethodType.AMAZON_PAY);
//        paymentTypes.add(SessionCreateParams.PaymentMethodType.PAYPAL);
        paymentTypes.add(SessionCreateParams.PaymentMethodType.CARD);
        params.put("payment_method_types", paymentTypes);
        //initiating payment
        PaymentIntent intent = PaymentIntent.create(params);
        if(intent ==null)
            throw new ServerDownException("Payment got failed from Stripe, Please try again! ");
        PaymentResponse response=new PaymentResponse();
        response.setStatus(intent.getStatus());
        response.setGatewayTxnId(intent.getId());
        return response;
    }
}
