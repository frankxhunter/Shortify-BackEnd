package com.shortify.configs;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

@Dependent
public class ProducersResources {

    @Resource(name= "jdbc/ConnectionMySQL")
    private DataSource ds;

    @Produces
    @RequestScoped
    @MySQLConnection
    private Connection beanConnection() throws SQLException{
        return ds.getConnection();
    }

    public void close(@Disposes @MySQLConnection Connection connection) throws SQLException{
        connection.close();
    }
}
