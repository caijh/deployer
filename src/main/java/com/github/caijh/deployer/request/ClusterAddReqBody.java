package com.github.caijh.deployer.request;

import lombok.Data;

@Data
public class ClusterAddReqBody {

    private String name;

    private String kubeApiserver;

}
