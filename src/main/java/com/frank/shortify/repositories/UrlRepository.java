package com.frank.shortiy.shortify.repositories;

import com.frank.shortiy.shortify.models.Url;
import com.frank.shortiy.shortify.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortUrl);

    Iterable<Url> findByUser(User user);
}
