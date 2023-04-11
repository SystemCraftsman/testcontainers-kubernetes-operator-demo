package com.systemcraftsman.kubegame.service;

import com.systemcraftsman.kubegame.customresource.Game;
import com.systemcraftsman.kubegame.customresource.World;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class WorldService {

    private static final String POSTGRES_SUFFIX = "-postgres";
    private static final String WORLD_SUFFIX = "-world";

    @Inject
    private GameService gameService;

    @Inject
    private PostgresService postgresService;

    public void createWorldTableIfNotExists(Game game) {
        try {
            DataSource dataSource = postgresService.createDatasource(game.getMetadata().getName() + POSTGRES_SUFFIX + ":5432",
                    "postgres", game.getSpec().getDatabase().getUsername(), game.getSpec().getDatabase().getPassword());
            Connection connection = dataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS World (name VARCHAR(50), game VARCHAR(50), description VARCHAR(1000));");

            preparedStatement.execute();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createWorldRecordIfNotExists(World world, Game game) {
        try {
            DataSource dataSource = postgresService.createDatasource(game.getMetadata().getName() + POSTGRES_SUFFIX + ":5432",
                    "postgres", game.getSpec().getDatabase().getUsername(), game.getSpec().getDatabase().getPassword());
            Connection connection = dataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT FROM World WHERE name=? and game=?");
            preparedStatement.setString(1, world.getMetadata().getName());
            preparedStatement.setString(2, world.getSpec().getGame());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() == false) {
                resultSet.close();
                preparedStatement.close();

                PreparedStatement preparedStatementForInsert = connection.prepareStatement(
                        "INSERT INTO World (name, game, description) VALUES (?, ?, ?)");
                preparedStatementForInsert.setString(1, world.getMetadata().getName());
                preparedStatementForInsert.setString(2, world.getSpec().getGame());
                preparedStatementForInsert.setString(3, world.getSpec().getDescription());
                preparedStatementForInsert.execute();
                preparedStatementForInsert.close();
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
