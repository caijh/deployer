package com.github.caijh.deployer.service;

import java.util.List;

import com.github.caijh.deployer.model.Cluster;
import org.springframework.web.multipart.MultipartFile;

public interface ClusterService {

    List<Cluster> list();

    void add(Cluster cluster, MultipartFile kubeconfig) throws Exception;

}
