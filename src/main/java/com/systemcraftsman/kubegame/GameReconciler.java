package com.systemcraftsman.kubegame;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

public class GameReconciler implements Reconciler<Game> { 
  private final KubernetesClient client;

  public GameReconciler(KubernetesClient client) {
    this.client = client;
  }

  // TODO Fill in the rest of the reconciler

  @Override
  public UpdateControl<Game> reconcile(Game resource, Context context) {
    // TODO: fill in logic

    return UpdateControl.noUpdate();
  }
}

