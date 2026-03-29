package com.raj.ecommerce.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailService(
            JavaMailSender sender,
            @Value("${spring.mail.username}") String fromAddress
    ){
        this.mailSender=sender;
        this.fromAddress = fromAddress;
    }
     public void  sendEmail(String to,String subject,String body){
        try {
            if (fromAddress == null || fromAddress.isBlank()) {
                log.error("Mail not sent because sender address is not configured. Set env MAIL_USERNAME was null ");
                return;
            }
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
        } catch (Exception ex) {
            log.error("Exception occurred while sending mail: ",ex);

        }
     }

    public void sendHtmlEmail(String to,String subject,String body) {
        try {
            if (fromAddress == null || fromAddress.isBlank()) {
                log.error("Mail not sent because sender address is not configured.");
                return;
            }
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            // true for making HTML enable
            helper.setText(body, true );

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Exception occurred while sending sendHtmlEmail mail: ",e);
        }
    }
}
