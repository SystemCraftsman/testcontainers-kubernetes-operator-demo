package com.systemcraftsman.kubegame.service;

import org.postgresql.ds.PGSimpleDataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.IntStream;

@ApplicationScoped
public class PostgresService {

    private DataSource createDatasource(String host, String dbName, String username, String password) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(host);
        dataSource.setDatabaseName(dbName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    public void execute(String host, String dbName, String username, String password, String query, String... params){
        try {
            Connection connection = createDatasource(host, dbName, username, password).getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            IntStream.range(0, params.length)
                    .forEach(idx ->
                    {
                        try {
                            preparedStatement.setString(idx + 1, params[idx]);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

            preparedStatement.execute();

            connection.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeQuery(String host, String dbName, String username, String password, String query, String... params){
        try {
            Connection connection = createDatasource(host, dbName, username, password).getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            IntStream.range(0, params.length)
                    .forEach(idx ->
                    {
                        try {
                            preparedStatement.setString(idx + 1, params[idx]);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
