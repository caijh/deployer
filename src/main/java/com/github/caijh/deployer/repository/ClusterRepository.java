package com.github.caijh.deployer.repository;

import com.github.caijh.deployer.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClusterRepository extends JpaRepository<Cluster, String> {

    Cluster findByName(String name);

}
