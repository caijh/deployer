package com.github.caijh.deployer.controller;

import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;

import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.request.ClusterAddReqBody;
import com.github.caijh.deployer.service.ClusterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 集群控制器.
 *
 * @author caijunhui
 */
@RestController
public class ClusterController {

    @Inject
    private ClusterService clusterService;

    /**
     * 获取当前集群列表.
     *
     * @return 集群列表
     */
    @GetMapping(value = "/clusters")
    public List<Cluster> clusters() {
        return clusterService.list();
    }

    /**
     * 新增集群.
     *
     * @param reqBody ClusterAddReqBody
     * @param file    kubeconfig 文件
     * @throws Exception if add cluster fail.
     */
    @PostMapping(value = "/cluster")
    public void add(@ModelAttribute @Valid ClusterAddReqBody reqBody, @RequestParam(value = "kubeconfig") MultipartFile file) throws Exception {
        Cluster cluster = new Cluster();
        cluster.setName(reqBody.getName());
        cluster.setKubeApiserver(reqBody.getKubeApiserver());

        clusterService.add(cluster, file);
    }

}
