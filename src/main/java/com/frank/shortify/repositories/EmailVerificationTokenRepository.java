package com.frank.shortify.repositories;

import com.frank.shortify.models.EmailVerificationToken;
import com.frank.shortify.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUser(User user);

    void deleteByUser(User user);
}

