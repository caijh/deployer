package com.github.caijh.deployer.controller;

import java.util.UUID;
import javax.inject.Inject;

import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.request.AppCreateReq;
import com.github.caijh.deployer.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Inject
    private AppService appService;

    /**
     * 创建app.
     *
     * @param req 请求信息
     * @return string
     * @throws Exception if create fail.
     */
    @PostMapping(value = "/app")
    @ResponseBody
    public ResponseEntity<String> create(@RequestBody AppCreateReq req) throws Exception {
        App app = new App();
        app.setId(UUID.randomUUID().toString());
        app.setName(req.getName());
        app.setClusterId(req.getClusterId());
        app.setNamespace(req.getTargetNamespace());
        app.setChartName(req.getChartName());
        app.setChartVersion(req.getChartVersion());
        app.setValuesJson(req.getValuesJson());

        appService.create(app);

        return ResponseEntity.ok("create successful");
    }

}
