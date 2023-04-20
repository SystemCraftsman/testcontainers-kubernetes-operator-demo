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
    }

    //TODO: Stop the operator instance
    @AfterAll
    void stopOperator() {
    }

    //TODO: Add a test for a Game object creation and assert its status and the dependants
    @Test
    @Order(1)
    public void testGame() {}

    //TODO: Add a test for World objects creation and assert their status and the dependants
    @Test
    @Order(2)
    public void testWorld() throws SQLException {}

    //TODO: Add a test for deletion of the World and Game objects and assert their dependants are deleted
    @Test
    @Order(3)
    public void testDeletion() {}

}
