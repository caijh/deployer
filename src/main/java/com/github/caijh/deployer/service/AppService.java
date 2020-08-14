package com.github.caijh.deployer.service;

import java.util.Optional;

import com.github.caijh.deployer.model.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppService {

    /**
     * create app.
     *
     * @param app App to create
     * @throws Exception if create fail.
     */
    void create(App app) throws Exception;

    /**
     * 应用分页.
     *
     * @param pageable Pageable
     * @return Page of App
     */
    Page<App> list(Pageable pageable);

    Optional<App> getByName(String clusterId, String appName);

}
