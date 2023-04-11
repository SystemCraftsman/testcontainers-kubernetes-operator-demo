package com.systemcraftsman.kubegame.reconciler;

import com.systemcraftsman.kubegame.customresource.Game;
import com.systemcraftsman.kubegame.service.GameService;
import com.systemcraftsman.kubegame.status.GameStatus;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import javax.inject.Inject;

public class GameReconciler implements Reconciler<Game> {
    private final KubernetesClient client;

    @Inject
    private GameService gameService;

    public GameReconciler(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public UpdateControl<Game> reconcile(Game resource, Context context) {

        Deployment postgresDeployment = gameService.getPostgresDeployment(client, resource);
        Service postgresService = gameService.getPostgresService(client, resource);

        if (postgresDeployment == null) {
            postgresDeployment = gameService.createPostgresDeployment(client, resource);
        }

        if (postgresService == null) {
            gameService.createPostgresService(client, resource);
        }

        if(postgresDeployment.getStatus().getReadyReplicas() == postgresDeployment.getStatus().getReplicas()){
            GameStatus status = new GameStatus();
            status.setReady(true);
            resource.setStatus(status);
            return UpdateControl.updateStatus(resource);
        }

        return UpdateControl.noUpdate();
    }
}

