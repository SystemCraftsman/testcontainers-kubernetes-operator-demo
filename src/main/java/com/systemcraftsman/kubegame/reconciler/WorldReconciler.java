package com.systemcraftsman.kubegame.reconciler;

import com.systemcraftsman.kubegame.customresource.Game;
import com.systemcraftsman.kubegame.customresource.World;
import com.systemcraftsman.kubegame.service.GameService;
import com.systemcraftsman.kubegame.service.WorldService;
import com.systemcraftsman.kubegame.status.WorldStatus;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;

import javax.inject.Inject;
import java.time.Duration;

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_CURRENT_NAMESPACE;

public class WorldReconciler implements Reconciler<World> {
    private final KubernetesClient client;

    @Inject
    private WorldService worldService;

    public WorldReconciler(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public UpdateControl<World> reconcile(World resource, Context<World> context) {
        final String worldName = resource.getMetadata().getName();

        Game game = client.resources(Game.class)
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(resource.getSpec().getGame())
                .get();

        if (game != null) {
            WorldStatus status = new WorldStatus();
            status.setReady(true);
            resource.setStatus(status);

            worldService.createWorldTableIfNotExists(game);
            worldService.createWorldRecordIfNotExists(resource, game);

            return UpdateControl.updateStatus(resource);
        } else {
            final Duration duration = Duration.ofSeconds(5);
            WorldStatus status = new WorldStatus();
            status.setMsg(String.format("World %s is not ready yet, rescheduling reconciliation after %d secs", worldName, duration.toSeconds()));
            resource.setStatus(status);
            return UpdateControl.updateStatus(resource).rescheduleAfter(duration);
        }
    }
}

