package com.systemcraftsman.kubegame.customresource;

import com.systemcraftsman.kubegame.spec.WorldSpec;
import com.systemcraftsman.kubegame.status.WorldStatus;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("kubegame.systemcraftsman.com")
public class World extends CustomResource<WorldSpec, WorldStatus> implements Namespaced {}

