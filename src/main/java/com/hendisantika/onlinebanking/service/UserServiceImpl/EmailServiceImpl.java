package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username:noreply@onlinebanking.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Async
    @Override
    public void sendTransactionNotification(User user, String transactionType, double amount, String accountType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Transaction Alert - " + transactionType);
            message.setText(String.format(
                    "Dear %s %s,\n\n" +
                    "A %s transaction of $%.2f has been processed on your %s account.\n\n" +
                    "If you did not authorize this transaction, please contact us immediately.\n\n" +
                    "Thank you for banking with us.\n\n" +
                    "Best regards,\n" +
                    "Online Banking Team",
                    user.getFirstName(), user.getLastName(),
                    transactionType.toLowerCase(), amount, accountType
            ));
            mailSender.send(message);
            log.info("Transaction notification email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send transaction notification email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Password Reset Request");
            message.setText(String.format(
                    "Dear %s %s,\n\n" +
                    "We received a request to reset your password.\n\n" +
                    "Click the link below to reset your password:\n" +
                    "%s/password/reset?token=%s\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Online Banking Team",
                    user.getFirstName(), user.getLastName(),
                    baseUrl, resetToken
            ));
            mailSender.send(message);
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Online Banking");
            message.setText(String.format(
                    "Dear %s %s,\n\n" +
                    "Welcome to Online Banking!\n\n" +
                    "Your account has been successfully created. You can now log in and start managing your finances.\n\n" +
                    "Features available:\n" +
                    "- View account balances\n" +
                    "- Transfer funds\n" +
                    "- View transaction history\n" +
                    "- Download statements\n\n" +
                    "If you have any questions, please don't hesitate to contact us.\n\n" +
                    "Best regards,\n" +
                    "Online Banking Team",
                    user.getFirstName(), user.getLastName()
            ));
            mailSender.send(message);
            log.info("Welcome email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendLowBalanceAlert(User user, String accountType, double balance) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Low Balance Alert - " + accountType + " Account");
            message.setText(String.format(
                    "Dear %s %s,\n\n" +
                    "This is a notification that your %s account balance is low.\n\n" +
                    "Current Balance: $%.2f\n\n" +
                    "Please consider making a deposit to avoid any inconvenience.\n\n" +
                    "Best regards,\n" +
                    "Online Banking Team",
                    user.getFirstName(), user.getLastName(),
                    accountType, balance
            ));
            mailSender.send(message);
            log.info("Low balance alert sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send low balance alert to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendDailyLimitExceededAlert(User user, String limitType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Daily " + limitType + " Limit Alert");
            message.setText(String.format(
                    "Dear %s %s,\n\n" +
                    "A transaction was attempted that would exceed your daily %s limit.\n\n" +
                    "If you need to increase your limit, please contact customer support.\n\n" +
                    "If you did not attempt this transaction, please contact us immediately.\n\n" +
                    "Best regards,\n" +
                    "Online Banking Team",
                    user.getFirstName(), user.getLastName(),
                    limitType.toLowerCase()
            ));
            mailSender.send(message);
            log.info("Daily limit exceeded alert sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send daily limit exceeded alert to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
