package com.github.caijh.deployer.controller;

import java.io.IOException;
import java.util.UUID;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.github.caijh.deployer.exception.AppNotFoundException;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.request.AppCreateReq;
import com.github.caijh.deployer.request.AppsReqBody;
import com.github.caijh.deployer.service.AppService;
import com.github.caijh.deployer.service.ClusterService;
import io.fabric8.kubernetes.api.model.EventList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用控制器.
 *
 * @author caijunhui
 */
@RestController
public class AppController {

    @Inject
    private ClusterService clusterService;

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
    public ResponseEntity<App> create(@RequestBody AppCreateReq req) throws Exception {
        App app = new App();
        app.setId(UUID.randomUUID().toString());
        app.setName(req.getName());
        app.setClusterId(req.getClusterId());
        app.setNamespace(req.getTargetNamespace());
        app.setChartName(req.getChartName());
        app.setChartVersion(req.getChartVersion());
        app.setValuesJson(req.getValuesJson());

        appService.create(app);

        return ResponseEntity.ok(app);
    }

    /**
     * 删除应用实例.
     *
     * @param appId appId
     * @return message about delete
     */
    @DeleteMapping(value = "/app/{appId}")
    public ResponseEntity<String> delete(@PathVariable String appId) {
        appService.delete(appId);
        return ResponseEntity.ok("delete ok");
    }

    /**
     * 日志.
     *
     * @param response response
     * @param appId    appId
     * @throws IOException if fail.
     */
    @GetMapping(value = "/app/{appId}/logs")
    public void logs(HttpServletResponse response, @PathVariable String appId) throws IOException {
        appService.logs(appId, response);
    }

    /**
     * 应用实例列表.
     *
     * @param reqBody AppsReqBody
     * @return 应用实例分页信息
     */
    @PostMapping(value = "/apps")
    public Page<App> apps(@RequestBody AppsReqBody reqBody) {
        return appService.list(PageRequest.of(reqBody.getPageNo(), reqBody.getPageSize()));
    }

    /**
     * 应用下的Pod.
     *
     * @param appId 应用名称
     * @return PodList
     */
    @GetMapping(value = "/app/{appId}/pods")
    public PodList appPods(@PathVariable String appId) {
        App app = appService.getById(appId).orElseThrow(AppNotFoundException::new);

        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(app.getClusterId());
        return kubernetesClient.pods().inNamespace(app.getNamespace()).withLabel("app", app.getName()).list();
    }

    /**
     * 获取Pod的事件.
     *
     * @param appId   app id
     * @param podName pod name
     * @return EventList
     */
    @GetMapping(value = "/app/{appId}/pod/{podName}/events")
    public EventList appPodEvents(@PathVariable String appId, @PathVariable String podName) {
        App app = appService.getById(appId).orElseThrow(AppNotFoundException::new);

        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(app.getClusterId());

        return kubernetesClient.v1().events()
                               .inNamespace(app.getNamespace())
                               .withField("involvedObject.name", podName)
                               .list();
    }

}
