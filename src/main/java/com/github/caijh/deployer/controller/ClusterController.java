package com.github.caijh.deployer.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import com.github.caijh.deployer.cache.Cache;
import com.github.caijh.deployer.init.InformerInit;
import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.request.ClusterAddReqBody;
import com.github.caijh.deployer.request.ClustersReqBody;
import com.github.caijh.deployer.service.ClusterService;
import io.fabric8.kubernetes.api.model.ComponentStatusList;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.VersionInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 集群控制器.
 *
 * @author caijunhui
 */
@RestController
public class ClusterController {

    @Inject
    private ClusterService clusterService;

    @Inject
    private InformerInit informerInit;

    /**
     * 获取当前集群列表.
     *
     * @param reqBody ClustersReqBody
     * @return 集群列表
     */
    @PostMapping(value = "/clusters")
    public Page<Cluster> clusters(@RequestBody ClustersReqBody reqBody) {
        Page<Cluster> page = clusterService.list(PageRequest.of(reqBody.getPageNo(), reqBody.getPageSize()));
        page.getContent().forEach(e -> e.setNodes(Cache.Kubernetes.getNodeList(e.getId())));
        return page;
    }

    /**
     * 新增集群.
     *
     * @param reqBody ClusterAddReqBody
     * @param file    kubeconfig 文件
     * @throws Exception if add cluster fail.
     */
    @PostMapping(value = "/cluster")
    public void add(@ModelAttribute @Valid ClusterAddReqBody reqBody, @RequestParam(value = "kubeconfig") MultipartFile file) throws Exception {
        Cluster cluster = new Cluster();
        cluster.setName(reqBody.getName());
        cluster.setKubeApiserver(reqBody.getKubeApiserver());

        clusterService.add(cluster, file);

        informerInit.initCluster(cluster);
    }

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
     * 集群组件状态.
     *
     * @param clusterId 集群id
     * @return ComponentStatusList
     */
    @GetMapping(value = "/cluster/{clusterId}/componentStatuses")
    public ComponentStatusList componentStatuses(@PathVariable String clusterId) {
        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(clusterId);
        return kubernetesClient.componentstatuses().list();
    }

}
