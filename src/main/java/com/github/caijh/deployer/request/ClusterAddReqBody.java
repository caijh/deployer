package com.github.caijh.deployer.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ClusterAddReqBody {

    /**
     * 集群名称.
     */
    @NotNull
    private String name;

    /**
     * kube apiserver url.
     */
    @NotNull
    private String kubeApiserver;

}
