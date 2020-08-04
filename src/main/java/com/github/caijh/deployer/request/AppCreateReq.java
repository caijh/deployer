package com.github.caijh.deployer.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class AppCreateReq {

    private String name;
    private String targetNamespace;
    private String chartName;
    private String chartVersion;

    private JSONObject valuesJson;

}
