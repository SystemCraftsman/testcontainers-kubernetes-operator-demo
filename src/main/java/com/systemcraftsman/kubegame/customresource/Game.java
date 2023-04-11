package com.systemcraftsman.kubegame.customresource;

import com.systemcraftsman.kubegame.spec.GameSpec;
import com.systemcraftsman.kubegame.status.GameStatus;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("kubegame.systemcraftsman.com")
public class Game extends CustomResource<GameSpec, GameStatus> implements Namespaced {

    @Override
    protected GameSpec initSpec() {
        return new GameSpec();
    }

    @Override
    protected GameStatus initStatus() {
        return new GameStatus();
    }

}

