package com.shortify.Services;

import java.sql.SQLException;

import com.shortify.models.Url;
import com.shortify.repositories.UrlRepository;
import com.shortify.utils.ServiceJDBCException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UrlService {

    @Inject
    UrlRepository urlRepository;

    public String getOriginalUrl(String shortUrl) {
        String originalURL = "";
        try {
            Url url = urlRepository.findByShortURL(shortUrl);
            if (url != null) {
                originalURL = url.getOrginalUrl();
            }
        } catch (SQLException e) {
            throw new ServiceJDBCException(e.getMessage(), e);
        }
        return originalURL;
    }

    public String generateUrlAndSave(String originalUrl) {
        String shortUrl = com.shortify.utils.Utils.generateHash(originalUrl);

        if (shortUrl != null) {
            Url url = new Url();
            url.setOrginalUrl(originalUrl);
            url.setShortUrl(shortUrl);

            try {
                urlRepository.save(url);
            } catch (SQLException e) {
                throw new ServiceJDBCException(e.getMessage(), e);
            }
        }

        return shortUrl;
    }
}
