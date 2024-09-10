package com.shortify.Services;

import java.sql.SQLException;
import java.util.List;

import com.shortify.models.Url;
import com.shortify.models.User;
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

    public String generateUrlAndSave(String originalUrl, User user) {
        int user_id = 0;
        if(user != null){
            user_id = user.getId();
        }
        String shortUrl = com.shortify.utils.Utils.generateHash(originalUrl, user_id);

        if (shortUrl != null) {
            Url url = new Url();
            url.setOrginalUrl(originalUrl);
            url.setShortUrl(shortUrl);
            url.setUser_id(user_id);

            try {
                urlRepository.save(url);
            } catch (SQLException e) {
                throw new ServiceJDBCException(e.getMessage(), e);
            }
        }

        return shortUrl;
    }

    public List<Url> getListUrlsByUser(User user){
        List<Url> list = null;
        if(user != null){
            try {
                list = urlRepository.getUrlsByUser(user.getId());
            } catch (SQLException e) {
                throw new ServiceJDBCException(e.getMessage(), e);
            }
        }
        return list;
    }
}
