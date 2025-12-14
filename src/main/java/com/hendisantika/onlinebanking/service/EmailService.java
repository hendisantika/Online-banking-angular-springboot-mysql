package com.hendisantika.onlinebanking.service;

import com.hendisantika.onlinebanking.entity.User;

public interface EmailService {

    void sendTransactionNotification(User user, String transactionType, double amount, String accountType);

    void sendPasswordResetEmail(User user, String resetToken);

    void sendWelcomeEmail(User user);

    void sendLowBalanceAlert(User user, String accountType, double balance);

    void sendDailyLimitExceededAlert(User user, String limitType);
}
