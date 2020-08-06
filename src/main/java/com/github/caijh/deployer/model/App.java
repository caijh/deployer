package com.github.caijh.deployer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.hibernate.annotations.Type;


@Data
@Entity
public class App {

    @Id
    private String id;
    private String name;
    private String clusterId;
    private String namespace;

    private Integer revision;

    private String chartName;
    private String chartVersion;

    @Type(type = "json")
    private JSONObject valuesJson;

}
