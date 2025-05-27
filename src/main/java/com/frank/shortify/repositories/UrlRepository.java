package com.frank.shortify.repositories;

import com.frank.shortify.models.Url;
import com.frank.shortify.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortUrl);

    Iterable<Url> findByUser(User user);
}
