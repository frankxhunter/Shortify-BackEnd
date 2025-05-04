package com.frank.shortiy.shortify.services;

import com.frank.shortiy.shortify.Utils.UrlHasher;
import com.frank.shortiy.shortify.models.Url;
import com.frank.shortiy.shortify.models.User;
import com.frank.shortiy.shortify.repositories.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {
    @Autowired
    private UrlRepository repository;

    public Iterable<Url> getAll(User user) {
        return repository.findByUser(user);
    }

    public Url saveUrl(String originalUrl, User user) {
        Url url;
        if (user != null) {
            url = getUrlwithHash(originalUrl, user.getEmail());
            url.setUser(user);
        } else {
            url = getUrlwithHash(originalUrl, "");
        }
        Optional<Url> urlFinded = repository.findByShortUrl(url.getShortUrl());
        return urlFinded.orElseGet(() -> repository.save(url));
    }

    public Optional<Url> findUrlByShortUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    private Url getUrlwithHash(String originalUrl, String email) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(UrlHasher.generateHash(email + originalUrl));
        return url;
    }

    public Optional<Url> findUrl(String email, long id) {
        return repository.findById(id)
                .filter(url -> url.getUser().getEmail().equals(email));
    }
}
