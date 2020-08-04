package com.github.caijh.deployer.controller;

import java.io.IOException;
import java.util.Map;

import com.github.caijh.deployer.request.AppCreateReq;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    private JinjavaConfig jinjavaConfig;

    @PostMapping(value = "/app")
    @ResponseBody
    public String create(@RequestBody AppCreateReq req) throws IOException {
        Jinjava jinjava = new Jinjava(jinjavaConfig);
        Map<String, Object> context = Maps.newHashMap();
        context.put("appName", req.getName());
        context.putAll(req.getValuesJson());
        String template = Resources.toString(Resources.getResource("static/charts/mysql/templates/svc.yaml"), Charsets.UTF_8);
        return jinjava.render(template, context);
    }

}
