package com.systemcraftsman.kubegame.test;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Map;

public class K3sResource implements QuarkusTestResourceLifecycleManager {

    //Initializes the K3sContainer instance.
    //It uses the Docker image "rancher/k3s:v1.24.12-k3s1"
    static K3sContainer k3sContainer = new K3sContainer(
            DockerImageName.parse("rancher/k3s:v1.24.12-k3s1"));

    //Start method is one of the methods in the interface QuarkusTestResourceLifecycleManager
    //This method runs when a test lifecycle is started
    //In this case, it is used for starting the container and setting the kubeConfigYaml value into a property.
    @Override
    public Map<String, String> start() {
        k3sContainer.start();
        return Collections.singletonMap(
                "kubeConfigYaml", k3sContainer.getKubeConfigYaml()
        );
    }

    //Stop method is one of the methods in the interface QuarkusTestResourceLifecycleManager
    //This method runs when a test lifecycle is stopped
    //In this case, it is used for stopping the container
    @Override
    public void stop() {
        k3sContainer.stop();
    }
}
