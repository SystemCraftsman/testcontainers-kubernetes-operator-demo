package com.systemcraftsman.kubegame.test;

import io.fabric8.kubernetes.client.Config;
import io.quarkus.arc.Priority;
import io.quarkus.kubernetes.client.runtime.KubernetesClientBuildConfig;
import io.quarkus.kubernetes.client.runtime.KubernetesClientUtils;
import io.quarkus.kubernetes.client.runtime.KubernetesConfigProducer;
import io.quarkus.runtime.TlsConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Alternative
@Priority(1)
@Singleton
public class K3sConfigProducer extends KubernetesConfigProducer {

    @ConfigProperty(name = "kubeConfigYaml")
    String kubeConfigYaml;

    @Singleton
    @Produces
    public Config config(KubernetesClientBuildConfig buildConfig, TlsConfig tlsConfig) {
        return Config.fromKubeconfig(kubeConfigYaml);
    }
}
