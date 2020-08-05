package com.github.caijh.deployer.model;

import lombok.Data;

@Data
public class Cluster {
    private String id;
    private String name;
    private String kubeApiserver;
    private String kubeconfig;
}
