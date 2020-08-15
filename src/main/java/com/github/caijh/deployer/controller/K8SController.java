package com.github.caijh.deployer.controller;

import javax.inject.Inject;

import com.github.caijh.deployer.exception.AppNotFoundException;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.service.AppService;
import com.github.caijh.deployer.service.ClusterService;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.VersionInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * k8s 控制器.
 */
@RestController
public class K8SController extends BaseController {

    @Inject
    private ClusterService clusterService;

    @Inject
    private AppService appService;

    /**
     * 集群k8s版本信息.
     *
     * @param clusterId 集群id
     * @return VersionInfo
     */
    @GetMapping(value = "/cluster/{clusterId}/version")
    public VersionInfo version(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.getVersion();
    }

    /**
     * 集群节点信息.
     *
     * @param clusterId 集群id
     * @return NodeList
     */
    @GetMapping(value = "/cluster/{clusterId}/nodes")
    public NodeList nodes(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.nodes().list();
    }

    /**
     * 集群下的名称空间.
     *
     * @param clusterId 集群id
     * @return NamespaceList
     */
    @GetMapping(value = "/cluster/{clusterId}/namespaces")
    public NamespaceList namespaces(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.namespaces().list();
    }


    /**
     * 集群下所有Pod.
     *
     * @param clusterId 集群id
     * @return PodList
     */
    @GetMapping(value = "/cluster/{clusterId}/pods")
    public PodList clusterPods(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.pods().list();
    }

    /**
     * 集群下某一名称空间所有Pod.
     *
     * @param clusterId 集群id
     * @param namespace 名称空间
     * @return PodList
     */
    @GetMapping(value = "/cluster/{clusterId}/{namespace}/pods")
    public PodList namespacePods(@PathVariable String clusterId, @PathVariable String namespace) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.pods().inNamespace(namespace).list();
    }

    /**
     * 应用下的Pod.
     *
     * @param clusterId 集群id.
     * @param appId     应用名称
     * @return PodList
     */
    @GetMapping(value = "/cluster/{clusterId}/app/{appId}/pods")
    public PodList appPods(@PathVariable String clusterId, @PathVariable String appId) {
        App app = appService.getById(appId).orElseThrow(AppNotFoundException::new);

        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.pods().inNamespace(app.getNamespace()).list();
    }

}
