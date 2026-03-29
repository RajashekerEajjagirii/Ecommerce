package com.raj.ecommerce.util;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailBodyBuildTemplate {


    public String accountVerifyTemplate(String customerName, String verifyToken) {
        return "<html>"
                + "<body>"
                + "<p>Hello <strong>" + customerName + "</strong>,</p>"
                + "<p>You can verify your Email by clicking it here: "
                + "<a href='http://127.0.0.1:8000/api/verify/{verifyToken}'>Link</a></p></br>".replace("{verifyToken}",verifyToken)
                + "<span style='font-weight:bold; color:#2a7ae2;'>" + "Happy Shopping,have a nice day!" + "</span>.</p>"
                + "<p>Thanks,<br/>"
                + "Team Raj eCommerce</p>"
                + "</body>"
                + "</html>";
    }

    public String orderSuccessTemplate(String customerName, BigDecimal amount, String trackingNumber) {
        return "<html>"
                + "<body>"
                + "<p>Hello <strong>" + customerName + "</strong>,</p>"
                + "<p>Your order was placed successfully with payment of "
                + "Rupees <strong>" + amount + " INR</strong>.</p>"
                + "<p>You can find the shipment details by following tracking number: "
                + "<span style='font-weight:bold; color:#2a7ae2;'>" + trackingNumber + "</span>.</p>"
                + "<p>Thanks,<br/>"
                + "Team Raj eCommerce</p>"
                + "</body>"
                + "</html>";
    }

    public String orderFailureTemplate(String customerName, BigDecimal amount) {
        return "<html>"
                + "<body>"
                + "<p>Hello <strong>" + customerName + "</strong>,</p>"
                + "<p>Your order payment was failed with the amount of "
                + "<strong>Rupees " + amount + " INR</strong>.</p>"
                + "<p>Can you please try again later!. "
                + "<span style='font-weight:bold; color:#2a7ae2;'>" + "Sorry for the Inconvenience" + "</span>.</p>"
                + "<p>Thanks,<br/>"
                + "Team Raj eCommerce</p>"
                + "</body>"
                + "</html>";
    }

}
