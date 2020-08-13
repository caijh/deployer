package com.github.caijh.deployer.service;

import com.github.caijh.deployer.model.App;

public interface AppService {

    /**
     * create app.
     *
     * @param app App to create
     * @throws Exception if create fail.
     */
    void create(App app) throws Exception;

}
