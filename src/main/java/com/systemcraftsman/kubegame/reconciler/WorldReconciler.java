package com.systemcraftsman.kubegame.reconciler;

import com.systemcraftsman.kubegame.customresource.World;
import com.systemcraftsman.kubegame.customresource.Game;
import com.systemcraftsman.kubegame.service.GameService;
import com.systemcraftsman.kubegame.service.WorldService;
import com.systemcraftsman.kubegame.status.WorldStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import javax.inject.Inject;
import java.time.Duration;

public class WorldReconciler implements Reconciler<World> {

    @Inject
    KubernetesClient client;

    @Inject
    private WorldService worldService;

    @Inject
    private GameService gameService;

    @Override
    public UpdateControl<World> reconcile(World resource, Context<World> context) {
        Game game = gameService.getGame(resource.getSpec().getGame(), resource.getMetadata().getNamespace());

        client.resources(Game.class)
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(resource.getSpec().getGame())
                .get();


        if (game != null && game.getStatus().isReady()) {

            worldService.createWorldTableIfNotExists(game);
            worldService.createWorldRecordIfNotExists(resource, game);

            WorldStatus status = resource.getStatus();
            status.setReady(true);
            status.setMsg("All dependencies are ready");

            return UpdateControl.updateStatus(resource);
        }

        return UpdateControl.<World>noUpdate().rescheduleAfter(Duration.ofSeconds(5));
    }
}

