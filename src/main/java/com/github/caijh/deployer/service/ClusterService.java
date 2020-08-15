package com.github.caijh.deployer.service;

import com.github.caijh.deployer.model.Cluster;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ClusterService {

    Page<Cluster> list(Pageable pageable);

    void add(Cluster cluster, MultipartFile kubeconfig) throws Exception;

    KubernetesClient getKubernetesClient(String clusterId);

    KubernetesClient getKubernetesClient(Cluster cluster);

}
