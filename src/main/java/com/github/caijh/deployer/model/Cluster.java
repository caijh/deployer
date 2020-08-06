package com.github.caijh.deployer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Cluster {

    @Id
    private String id;
    private String name;
    private String kubeApiserver;

}
