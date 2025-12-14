package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PasswordResetToken;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PasswordResetTokenDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.service.EmailService;
import com.hendisantika.onlinebanking.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final PasswordResetTokenDao tokenDao;
    private final UserDao userDao;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(PasswordResetTokenDao tokenDao, UserDao userDao,
                                     EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
        this.tokenDao = tokenDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createPasswordResetToken(User user) {
        tokenDao.findByUser(user).ifPresent(token -> tokenDao.delete(token));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenDao.save(resetToken);

        emailService.sendPasswordResetEmail(user, token);
        log.info("Password reset token created for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void createPasswordResetTokenForEmail(String email) {
        User user = userDao.findByEmail(email);
        if (user != null) {
            createPasswordResetToken(user);
        } else {
            log.warn("Password reset requested for non-existent email: {}", email);
        }
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenDao.findByToken(token);
        if (resetToken.isEmpty()) {
            log.warn("Invalid password reset token: {}", token);
            return false;
        }

        PasswordResetToken passwordResetToken = resetToken.get();
        if (passwordResetToken.isExpired()) {
            log.warn("Expired password reset token for user: {}", passwordResetToken.getUser().getUsername());
            return false;
        }

        if (passwordResetToken.isUsed()) {
            log.warn("Already used password reset token for user: {}", passwordResetToken.getUser().getUsername());
            return false;
        }

        return true;
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return tokenDao.findByToken(token)
                .map(PasswordResetToken::getUser)
                .orElse(null);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetToken = tokenDao.findByToken(token);
        if (resetToken.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        PasswordResetToken passwordResetToken = resetToken.get();
        if (passwordResetToken.isExpired() || passwordResetToken.isUsed()) {
            throw new IllegalArgumentException("Token has expired or already been used");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);

        passwordResetToken.setUsed(true);
        tokenDao.save(passwordResetToken);

        log.info("Password reset successful for user: {}", user.getUsername());
    }
}
