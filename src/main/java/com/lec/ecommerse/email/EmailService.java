package com.lec.ecommerse.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String toEmail, String emailBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(emailBody, true);
            helper.setTo(toEmail);
            helper.setSubject("Confirm your email");
            helper.setFrom("suleymantm11@gmail.com");
            mailSender.send(mimeMessage);
            log.info("Mail sent successfully...");
        } catch (MessagingException e) {
            log.error("failed to send email ", e);
            throw new IllegalStateException("failed to send email");
        }

    }
}
