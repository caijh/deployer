package com.github.caijh.deployer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.alibaba.fastjson.JSONObject;
import com.github.caijh.framework.orm.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Type;


@Data
@Entity
public class App implements BaseEntity<String> {

    @Id
    private String id;
    private String name;
    /**
     * 集群id.
     */
    private String clusterId;
    /**
     * K8S命名空间.
     */
    private String namespace;

    /**
     * 当前部署版本.
     */
    private Integer revision;

    private String chartName;
    private String chartVersion;

    @Type(type = "json")
    private JSONObject valuesJson;

}
