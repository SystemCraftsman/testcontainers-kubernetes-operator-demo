package com.systemcraftsman.kubegame.service;

import org.postgresql.ds.PGSimpleDataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@ApplicationScoped
public class PostgresService {

    public DataSource createDatasource(String host, String dbName, String username, String password) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(host);
        dataSource.setDatabaseName(dbName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }


}
