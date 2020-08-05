package com.github.caijh.deployer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;


@Data
@Entity
public class App {

    @Id
    private String id;
    private String name;
    private String clusterId;
    private String namespace;

    private String chartName;
    private String chartVersion;

}
