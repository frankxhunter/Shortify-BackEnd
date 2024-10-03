package com.shortify.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shortify.configs.MySQLConnection;
import com.shortify.models.InfoRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InfoRequestRepository {

    @Inject
    @MySQLConnection
    Connection conn;

    public List<InfoRequest> getInfoRequestsByUrl(int id_url) throws SQLException {
        List<InfoRequest> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM info_request
                WHERE id_url = ?
                """)) {
            ps.setInt(1, id_url);

            ResultSet result = ps.executeQuery();
            while (result.next()) {
                list.add(getInfoRequest(result));
            }
        }
        return list;
    }

    public void save(InfoRequest infoRequest) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO info_request
                    (id_url, ip, browser, os, architecture, date)
                    VALUES (?, ?, ?, ?, ?, ? )
                    """)) {
                ps.setInt(1, infoRequest.getIdUrl());
                ps.setString(2, infoRequest.getIp());
                ps.setString(3, infoRequest.getBrowser());
                ps.setString(4, infoRequest.getOs());
                ps.setString(5, infoRequest.getArchitecture());
                ps.setTimestamp(6, infoRequest.getDate());

                ps.executeUpdate();

            }
    }

    private InfoRequest getInfoRequest(ResultSet result) throws SQLException {
        InfoRequest InfoRequest = new InfoRequest();

        InfoRequest.setIp(result.getString("ip"));
        InfoRequest.setBrowser(result.getString("browser"));
        InfoRequest.setOs(result.getString("os"));
        InfoRequest.setArchitecture(result.getString("architecture"));
        InfoRequest.setDate(result.getTimestamp("date"));

        return InfoRequest;
    }
}
