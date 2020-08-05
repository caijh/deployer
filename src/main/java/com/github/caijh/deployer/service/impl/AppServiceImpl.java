package com.github.caijh.deployer.service.impl;

import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.repository.AppRepository;
import com.github.caijh.deployer.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppRepository appRepository;

    @Override
    public void create(App app) {

        App existApp = appRepository.findByClusterIdAndNamespaceAndName(app.getClusterId(), app.getNamespace(), app.getName());

        if (existApp != null) {

        }
    }

}
