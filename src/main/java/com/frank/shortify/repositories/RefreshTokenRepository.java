package com.frank.shortify.repositories;

import com.frank.shortify.models.RefreshToken;
import com.frank.shortify.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenId(String tokenId);

    void deleteByUser(User user);
}
