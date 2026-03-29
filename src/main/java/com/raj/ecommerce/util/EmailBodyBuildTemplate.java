package com.raj.ecommerce.util;

import com.raj.ecommerce.config.properties.RajeCommerceProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailBodyBuildTemplate {

    private final RajeCommerceProperties properties;

    public EmailBodyBuildTemplate(RajeCommerceProperties properties) {
        this.properties = properties;
    }


    public String accountVerifyTemplate(String customerName, String emailOtp) {

        String baseUri = properties.getBaseUri();
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 1);
        }
        String url = baseUri + properties.getAccountVerifyUri().replace("{emailOtp}", emailOtp);
        return "<html>"
                + "<body>"
                + "<p>Hello <strong>" + customerName + "</strong>,</p>"
                + "<p>You can verify your Email by clicking it here: "
                + "<a href='{url}'>Link</a></p></br>".replace("{url}",url)
                + "<span style='font-weight:bold; color:#2a7ae2;'>" + "Happy Shopping,have a nice day!" + "</span>.</p>"
                + "<p>Thanks,<br/>"
                + "<strong>Team Raj eCommerce</strong></p>"
                + "</body>"
                + "</html>";
    }

    public String orderSuccessTemplate(String customerName, BigDecimal amount, String trackingNumber) {
        String trackUrl=properties.getBaseUri()+properties.getTrackDetailsUri().replace("{trackingNumber}",trackingNumber);
        return "<html>"
                + "<body>"
                + "<p>Hello <strong>" + customerName + "</strong>,</p>"
                + "<p>Your order was placed successfully with payment of "
                + "Rupees <strong>" + amount + " INR</strong>.</p>"
                + "<p>You can find the shipment details by following tracking number: "
                + "<span style='font-weight:bold; color:#2a7ae2;'>" + trackingNumber + "</span>.</p>"
                +"<p>Your shipment is ready. Track it here: <a href='{trackUrl}'>Link</a></p>".replace("{trackUrl}",trackUrl)
                + "<p>Thanks,<br/>"
                + "<strong>Team Raj eCommerce</strong></p>"
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
                + "<strong>Team Raj eCommerce</strong></p>"
                + "</body>"
                + "</html>";
    }

}
