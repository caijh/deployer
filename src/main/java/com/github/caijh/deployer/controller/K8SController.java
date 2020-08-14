package com.github.caijh.deployer.controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import com.github.caijh.commons.base.exception.BizRuntimeException;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.service.AppService;
import com.github.caijh.deployer.service.ClusterService;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.VersionInfo;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class K8SController {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Inject
    private ClusterService clusterService;

    @Inject
    private AppService appService;

    @GetMapping(value = "/cluster/{clusterId}/version")
    public VersionInfo version(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.getVersion();
    }

    @GetMapping(value = "/cluster/{clusterId}/nodes")
    public NodeList nodes(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.nodes().list();
    }

    @GetMapping(value = "/cluster/{clusterId}/namespaces")
    public NamespaceList namespaces(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.namespaces().list();
    }


    @GetMapping(value = "/cluster/{clusterId}/pods")
    public PodList listPod(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.pods().list();
    }

    @GetMapping(value = "/cluster/{clusterId}/app/{appName}/pods")
    public PodList listPod(@PathVariable String clusterId, @PathVariable String appName) {
        App app = appService.getByName(clusterId, appName).orElseThrow(BizRuntimeException::new);

        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.pods().inNamespace(app.getNamespace()).list();
    }

    @GetMapping(value = "/cluster/{clusterId}/deployments")
    public DeploymentList apps(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.apps().deployments().inAnyNamespace().list();
    }

    @GetMapping(value = "/cluster/{clusterId}/app/{appName}/watch")
    public void watch(@PathVariable String clusterId, @PathVariable String appName) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        final CountDownLatch closeLatch = new CountDownLatch(1);

        try (Watch watch = kubernetesClient.apps().deployments().watch(new Watcher<Deployment>() {
            @Override
            public void eventReceived(Action action, Deployment resource) {
                logger.info("{}: {}, target: {}", action, resource.getMetadata().getName(), appName.equalsIgnoreCase(resource.getMetadata().getName()));
            }

            @Override
            public void onClose(KubernetesClientException cause) {
                logger.debug("Watcher onClose");
                if (cause != null) {
                    logger.error(cause.getMessage(), cause);
                    closeLatch.countDown();
                }
            }
        })) {
            closeLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Could not watch resources", e);
        }

    }

}
