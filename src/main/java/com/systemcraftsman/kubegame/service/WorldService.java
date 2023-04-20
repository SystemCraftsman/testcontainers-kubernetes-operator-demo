package com.systemcraftsman.kubegame.service;

import com.systemcraftsman.kubegame.customresource.World;
import com.systemcraftsman.kubegame.customresource.Game;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WorldService {

    @Inject
    private GameService gameService;

    @Inject
    private PostgresService postgresService;

    @Inject
    KubernetesClient client;

    public World getWorld(String name, String namespace) {
        return client.resources(World.class)
                .inNamespace(namespace)
                .withName(name)
                .get();
    }

    public synchronized void createWorldTableIfNotExists(Game game) {
        postgresService.execute(gameService.getPostgresServiceName(game) + ":" + GameService.POSTGRES_DB_PORT,
                "postgres", game.getSpec().getDatabase().getUsername(), game.getSpec().getDatabase().getPassword(),
                "CREATE TABLE IF NOT EXISTS World (name VARCHAR(50), game VARCHAR(50), description VARCHAR(1000), PRIMARY KEY (name))");
    }

    public void createWorldRecordIfNotExists(World world, Game game) {
        postgresService.execute(gameService.getPostgresServiceName(game) + ":" + GameService.POSTGRES_DB_PORT,
                "postgres", game.getSpec().getDatabase().getUsername(), game.getSpec().getDatabase().getPassword(),
                "INSERT INTO World (name, game, description) VALUES (?, ?, ?) ON CONFLICT (name) DO NOTHING",
                world.getMetadata().getName(),
                world.getSpec().getGame(),
                world.getSpec().getDescription());
    }

}
