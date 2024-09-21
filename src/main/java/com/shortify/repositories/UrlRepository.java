package com.shortify.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shortify.configs.MySQLConnection;
import com.shortify.models.Url;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UrlRepository {

    @Inject
    @MySQLConnection
    Connection conn;

    public UrlRepository() {
    }

    private Url getUrl(ResultSet result) throws SQLException {
        Url url = new Url();
        url.setId(result.getInt("id"));
        url.setOriginalUrl(result.getString("originalurl"));
        url.setShortUrl(result.getString("shorturl"));
        return url;
    }

    public Url findByShortURL(String shorUrl) throws SQLException {
        Url url = null;
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM urls
                WHERE shortUrl = ?
                """)) {
            ps.setString(1, shorUrl);

            ResultSet result = ps.executeQuery();
            if (result.next()) {
                url = getUrl(result);
            }
        }
        return url;
    }

public List<Url> getUrlsByUser(int user_id) throws SQLException{
    List<Url> list = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement("""
        SELECT * FROM urls
        WHERE user_id = ?
        """)) {
    ps.setInt(1, user_id);

    ResultSet result = ps.executeQuery();
    while (result.next()) {
        list.add(getUrl(result)) ;
    }
}
    return list;
}

    public void save(Url url) throws SQLException {
        if (findByShortURL(url.getShortUrl()) == null) {
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO urls
                    (shorturl, originalurl, user_id)
                    VALUES (?, ?, ?)
                    """)) {
                ps.setString(1, url.getShortUrl());
                ps.setString(2, url.getOriginalUrl());
                ps.setInt(3, url.getUser_id());

                ps.executeUpdate();

            }
        }
    }
}
