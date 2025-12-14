package com.hendisantika.onlinebanking.service;

import com.hendisantika.onlinebanking.entity.User;

public interface PasswordResetService {

    void createPasswordResetToken(User user);

    void createPasswordResetTokenForEmail(String email);

    boolean validatePasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    void resetPassword(String token, String newPassword);
}
