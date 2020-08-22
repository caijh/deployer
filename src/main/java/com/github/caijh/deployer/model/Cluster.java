package com.github.caijh.deployer.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.github.caijh.framework.orm.model.BaseEntity;
import io.fabric8.kubernetes.api.model.Node;
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

    @Transient
    private List<Node> nodes;

}
