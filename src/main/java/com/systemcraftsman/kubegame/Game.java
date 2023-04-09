package com.systemcraftsman.kubegame;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("kubegame.systemcraftsman.com")
public class Game extends CustomResource<GameSpec, GameStatus> implements Namespaced {}

