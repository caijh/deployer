package com.github.caijh.deployer.service.impl;

import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

import com.github.caijh.deployer.config.props.ClustersProperties;
import com.github.caijh.deployer.exception.BizException;
import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.repository.ClusterRepository;
import com.github.caijh.deployer.service.ClusterService;
import com.google.common.io.Files;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Inject
    private ClusterRepository clusterRepository;

    @Override
    public List<Cluster> list() {
        return clusterRepository.findAll();
    }

    @Transactional
    @Override
    public void add(Cluster cluster, MultipartFile file) throws Exception {
        Cluster existCluster = clusterRepository.findByName(cluster.getName());
        if (existCluster != null) {
            throw new BizException("已存在同名的集群");
        }

        cluster.setId(UUID.randomUUID().toString());

        clusterRepository.save(cluster);

        File kubeconfig = new File(ClustersProperties.getClustersDir().getPath() + File.separator + cluster.getId() + File.separator + "kubeconfig");
        Files.createParentDirs(kubeconfig);
        file.transferTo(kubeconfig);
    }

}
