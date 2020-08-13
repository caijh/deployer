package com.github.caijh.deployer.request;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class AppCreateReq {

    /**
     * 应用名称.
     */
    @NotNull
    private String name;
    /**
     * 集群id.
     */
    @NotNull
    private String clusterId;
    /**
     * 命名空间.
     */
    @NotNull
    private String targetNamespace;
    /**
     * 应用模板名称.
     */
    @NotNull
    private String chartName;
    /**
     * 应用模板版本.
     */
    @NotNull
    private String chartVersion;

    /**
     * 部署配置值.
     */
    @NotNull
    private JSONObject valuesJson;

}
