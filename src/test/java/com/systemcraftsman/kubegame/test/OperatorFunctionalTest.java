package com.systemcraftsman.kubegame.test;

import com.systemcraftsman.kubegame.customresource.Game;
import com.systemcraftsman.kubegame.customresource.World;
import com.systemcraftsman.kubegame.service.GameService;
import com.systemcraftsman.kubegame.service.PostgresService;
import com.systemcraftsman.kubegame.service.WorldService;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

//TODO: Add @QuarkusTestResource annotation to enable the K3sResource for using the K3sContainer
@QuarkusTestResource(K3sResource.class)
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OperatorFunctionalTest {

    private static final String NAMESPACE = "default";

    @Inject
    Operator operator;

    @Inject
    KubernetesClient client;

    @Inject
    GameService gameService;

    @Inject
    WorldService worldService;

    @Inject
    PostgresService postgresService;

    //TODO: Start the operator instance
    @BeforeAll
    void startOperator() {
        operator.start();
    }

    //TODO: Stop the operator instance
    @AfterAll
    void stopOperator() {
        operator.stop();
    }

    //TODO: Add a test for a Game object creation and assert its status and the dependants
    @Test
    @Order(1)
    void testGame() {
        //Apply the oasis.yaml resource, which is a Game resource.
        client.resources(Game.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/oasis.yaml").getFile()).create();

        //Get the Game instance for "oasis"
        Game game = gameService.getGame("oasis", NAMESPACE);
        //Assert the "oasis" game is not null.
        Assert.assertNotNull(game);

        //Postgres deployment and its being ready takes time.
        //Wait for at most 60 seconds for the assertions.
        await().atMost(60, TimeUnit.SECONDS).untilAsserted(() -> {
            //Get the Postgres deployment instance of the related game.
            Deployment postgresDeployment = gameService.getPostgresDeployment(game);

            //Assert the deployment object's being not null
            Assert.assertNotNull(postgresDeployment);
            //Assert if the deployment is ready
            Assert.assertEquals(Integer.valueOf(1), postgresDeployment.getStatus().getReadyReplicas());
            //Assert if the "oasis" game status is ready
            Assert.assertTrue(gameService.getGame(game.getMetadata().getName(), NAMESPACE).getStatus().isReady());
        });

    }

    //TODO: Add a test for World objects creation and assert their status and the dependants
    @Test
    @Order(2)
    void testWorld() throws SQLException {
        //Apply the YAML resources for the worlds "archaide", "incipio", "chthonia"
        client.resources(World.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/archaide.yaml").getFile()).create();
        client.resources(World.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/incipio.yaml").getFile()).create();
        client.resources(World.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/chthonia.yaml").getFile()).create();

        //Get the world instances "archaide", "incipio", "chthonia"
        World worldArchaide = worldService.getWorld("archaide", NAMESPACE);
        World worldIncipio = worldService.getWorld("incipio", NAMESPACE);
        World worldChthonia = worldService.getWorld("chthonia", NAMESPACE);

        //Assert the world instances checking they are not null
        Assert.assertNotNull(worldArchaide);
        Assert.assertNotNull(worldIncipio);
        Assert.assertNotNull(worldChthonia);

        //Assert the world instances expecting their status is "Ready"
        //Wait for at most 60 seconds for the assertions.
        await().atMost(60, TimeUnit.SECONDS).untilAsserted(() -> {
            Assert.assertTrue(worldService.getWorld(worldArchaide.getMetadata().getName(), NAMESPACE).getStatus().isReady());
            Assert.assertTrue(worldService.getWorld(worldIncipio.getMetadata().getName(), NAMESPACE).getStatus().isReady());
            Assert.assertTrue(worldService.getWorld(worldChthonia.getMetadata().getName(), NAMESPACE).getStatus().isReady());
        });

        //Get the game from one of the worlds
        Game game = gameService.getGame(worldArchaide.getSpec().getGame(), worldArchaide.getMetadata().getNamespace());
        //Run a select query against the postgres instance for the World table
        ResultSet resultSet = postgresService.executeQuery(gameService.getPostgresServiceName(game) + ":" + GameService.POSTGRES_DB_PORT,
                "postgres", game.getSpec().getDatabase().getUsername(), game.getSpec().getDatabase().getPassword(),
                "SELECT * FROM World WHERE game=?",
                game.getMetadata().getName());

        //Iterate over the result set and assert if the records are in the database
        int resultCount = 0;
        while(resultSet.next()){
            resultCount++;
        }
        Assert.assertEquals(3, resultCount);

    }

    //TODO: Add a test for deletion of the World and Game objects and assert their dependants are deleted
    @Test
    @Order(3)
    void testDeletion() {
        //Delete the worlds "archaide", "incipio", "chthonia"
        client.resources(World.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/archaide.yaml").getFile()).delete();
        client.resources(World.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/incipio.yaml").getFile()).delete();
        client.resources(World.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/chthonia.yaml").getFile()).delete();

        //Assert the world instances are deleted
        //Wait for at most 60 seconds for the assertions.
        await().atMost(60, TimeUnit.SECONDS).untilAsserted(() -> {
            Assert.assertNull(worldService.getWorld("archaide", NAMESPACE));
            Assert.assertNull(worldService.getWorld("incipio", NAMESPACE));
            Assert.assertNull(worldService.getWorld("chthonia", NAMESPACE));
        });

        //Delete the "oasis" game
        client.resources(Game.class).inNamespace(NAMESPACE)
                .load(getClass().getResource("/examples/oasis.yaml").getFile()).delete();

        //Assert the game instance, its postgres instance and the service of it is deleted
        //Wait for at most 60 seconds for the assertions.
        await().atMost(60, TimeUnit.SECONDS).untilAsserted(() -> {
            Assert.assertNull(gameService.getGame("oasis", NAMESPACE));
            Assert.assertNull(client.apps().deployments().inNamespace(NAMESPACE).withName("oasis-postgres").get());
            Assert.assertNull(client.services().inNamespace(NAMESPACE).withName("oasis-postgres").get());
        });
    }

}
