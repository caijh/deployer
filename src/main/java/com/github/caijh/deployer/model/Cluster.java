package com.github.caijh.deployer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.caijh.framework.orm.model.BaseEntity;
import lombok.Data;

@Entity
@Data
public class Cluster implements BaseEntity<String> {

    @Id
    private String id;
    private String name;
    private String kubeApiserver;

}
