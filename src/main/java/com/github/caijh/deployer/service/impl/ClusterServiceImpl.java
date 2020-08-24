package com.github.caijh.deployer.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

import com.github.caijh.commons.base.exception.BizRuntimeException;
import com.github.caijh.deployer.config.props.ClustersProperties;
import com.github.caijh.deployer.exception.ClusterNotFoundException;
import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.repository.ClusterRepository;
import com.github.caijh.deployer.service.ClusterService;
import com.google.common.io.Files;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ClusterServiceImpl implements ClusterService {

    private final Map<String, KubernetesClient> kubernetesClients = new ConcurrentHashMap<>();


    @Inject
    private ClusterRepository clusterRepository;

    @Override
    public Page<Cluster> list(Pageable pageable) {
        return clusterRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public void add(Cluster cluster, MultipartFile file) throws Exception {
        Cluster existCluster = clusterRepository.findByName(cluster.getName());
        if (existCluster != null) {
            throw new BizRuntimeException("已存在同名的集群");
        }

        cluster.setId(UUID.randomUUID().toString());

        clusterRepository.save(cluster);

        File kubeconfig = new File(ClustersProperties.getClustersDir().getPath() + File.separator + cluster.getId() + File.separator + "kubeconfig");
        Files.createParentDirs(kubeconfig);
        file.transferTo(kubeconfig);
    }

    @Override
    public KubernetesClient getKubernetesClient(String clusterId) {
        KubernetesClient kubernetesClient = kubernetesClients.get(clusterId);
        if (kubernetesClient != null) {
            return kubernetesClient;
        }

        Cluster cluster = clusterRepository.findById(clusterId).orElseThrow(ClusterNotFoundException::new);

        kubernetesClient = this.getKubernetesClient(cluster);

        kubernetesClients.put(clusterId, kubernetesClient);

        return kubernetesClient;
    }

    @Override
    public KubernetesClient getKubernetesClient(Cluster cluster) {
        KubernetesClient kubernetesClient = kubernetesClients.get(cluster.getId());
        if (kubernetesClient != null) {
            return kubernetesClient;
        }

        try {
            File kubeconfig = new File(ClustersProperties.getClustersDir().getPath() + File.separator + cluster.getId() + File.separator + "kubeconfig");
            Config config = Config.fromKubeconfig(Files.asCharSource(kubeconfig, UTF_8).read());
            kubernetesClient = new DefaultKubernetesClient(config);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }

        kubernetesClients.put(cluster.getId(), kubernetesClient);

        return kubernetesClient;
    }

}
