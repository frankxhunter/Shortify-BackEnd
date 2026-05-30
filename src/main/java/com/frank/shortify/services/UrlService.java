package com.frank.shortify.services;

import com.frank.shortify.Utils.UrlHasher;
import com.frank.shortify.dto.CreateUrlDto;
import com.frank.shortify.exceptions.ResourceNotFoundException;
import com.frank.shortify.models.Url;
import com.frank.shortify.models.User;
import com.frank.shortify.repositories.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final static Logger log = LoggerFactory.getLogger(UrlService.class);
    @Autowired
    private UrlRepository repository;

    public Iterable<Url> getAll(User user) {
        return repository.findByUser(user);
    }

    public Url saveUrl(CreateUrlDto originalUrl, User user) {
        Url url;
        if (user != null) {
            url = getUrlWithHash(originalUrl, user.getEmail());
            url.setUser(user);
        } else {
            url = getUrlWithHash(originalUrl, "");
        }

        Optional<Url> urlFinded = repository.findByShortUrl(url.getShortUrl());
        Url urlResult = urlFinded.orElseGet(() -> repository.save(url));
        log.info("Url created: {}", urlResult);
        return urlResult;
    }

    public Optional<Url> findUrlByShortUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    private Url getUrlWithHash(CreateUrlDto originalUrl, String email) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl.getUrl());
        url.setShortUrl(UrlHasher.generateHash(email + originalUrl.getUrl() + originalUrl.getName()));
        url.setName(originalUrl.getName());
        url.setCreationDate(LocalDateTime.now());
        url.setClickCounter(0);
        return url;
    }

    public Optional<Url> findUrl(String email, long id) {
        return repository.findById(id)
                .filter(url -> url.getUser() != null ? url.getUser().getEmail().equals(email) : false);
    }

    public Url updateUrl(Long id, String urlNewOriginal, User user) {
        Optional<Url> urlFinded = findUrl(user.getEmail(), id);
        return urlFinded
                .filter(url -> url.getUser().getEmail().equals(user.getEmail()))
                .map(url -> {
                    url.setOriginalUrl(urlNewOriginal);
                    return repository.save(url);
                })
                .orElseThrow(() -> new ResourceNotFoundException("The url with the id: " + id + " not found"));

    }

    public void incrementClickCounter(Url url) {
        url.setClickCounter(url.getClickCounter() + 1);
        repository.save(url);
    }

    public void deleteUrl(Long id, User user) {
        Optional<Url> urlFinded = findUrl(user.getEmail(), id);
        urlFinded
                .filter(url -> url.getUser().getEmail().equals(user.getEmail()))
                .ifPresent(url -> repository.delete(url));
    }
}
