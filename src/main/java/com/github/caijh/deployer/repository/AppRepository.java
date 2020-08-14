package com.github.caijh.deployer.repository;

import java.util.Optional;

import com.github.caijh.deployer.model.App;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, String> {

    App findByClusterIdAndNamespaceAndName(String clusterId, String namespace, String name);

    Optional<App> findByClusterIdAndName(String clusterId, String appName);

}
