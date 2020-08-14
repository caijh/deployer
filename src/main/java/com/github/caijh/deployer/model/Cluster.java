package com.github.caijh.deployer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.caijh.framework.orm.model.BaseEntity;
import lombok.Data;

@Entity
@Data
public class Cluster implements BaseEntity<String> {

    /**
     * 集群id.
     */
    @Id
    private String id;

    /**
     * 集群名称，唯一.
     */
    @Column(unique = true)
    private String name;

    /**
     * 集群apiserver地址.
     */
    private String kubeApiserver;

}
