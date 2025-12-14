package com.hendisantika.onlinebanking.repository;

import com.hendisantika.onlinebanking.entity.PasswordResetToken;
import com.hendisantika.onlinebanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenDao extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    void deleteByUser(User user);
}
