package com.github.caijh.deployer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.caijh.framework.orm.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Cluster extends BaseEntity<String> {

    /**
     * 集群id.
     */
    @Id
    private String id;

    /**
     * 集群名称，唯一.
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * 集群apiserver地址.
     */
    @Column(nullable = false)
    private String kubeApiserver;

    private String authToken;

}
