package com.shortify.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.shortify.configs.MySQLConnection;
import com.shortify.models.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserRepository {

    @Inject
    @MySQLConnection
    private Connection con;

    public User getUserByUsername(String userName) throws SQLException {
        User user = null;
        if (userName != null) {

            try (PreparedStatement ps = con.prepareStatement("""
                    Select id, username, email, password
                    FROM users
                    WHERE username = ?
                    """)) {
                ps.setString(1, userName);
                ResultSet result = ps.executeQuery();

                if (result.next()) {
                    user = getUser(result);
                }
            }
        }
        return user;

    }

    public User getUserByEmail(String email) throws SQLException {
        User user = null;

        if (email != null) {

            try (PreparedStatement ps = con.prepareStatement("""
                    Select id, username, email, password
                    FROM users
                    WHERE email = ?
                    """)) {
                ps.setString(1, email);
                ResultSet result = ps.executeQuery();

                if (result.next()) {
                    user = getUser(result);
                }
            }
        }
        return user;

    }

    public void createUser(User user, String hashedPassword) throws SQLException {
        if (user != null) {

            try (PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO users
                    (username, email, password)
                    values (?, ?, ?)
                    """)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, hashedPassword);

                ps.executeUpdate();

            }
        }
    }

    private User getUser(ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getInt("id"));
        user.setUsername(result.getString("username"));
        user.setEmail(result.getString("email"));
        user.setPassword(result.getString("password"));
        return user;

    }

}
