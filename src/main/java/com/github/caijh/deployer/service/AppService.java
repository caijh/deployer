package com.github.caijh.deployer.service;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

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

    boolean delete(String appId);

    Optional<App> getById(String appId);

    void logs(String appId, HttpServletResponse response) throws IOException;

}
