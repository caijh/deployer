package com.github.caijh.deployer.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;

import com.github.caijh.deployer.cmd.Kubectl;
import com.github.caijh.deployer.cmd.ProcessResult;
import com.github.caijh.deployer.config.props.AppsProperties;
import com.github.caijh.deployer.exception.BizException;
import com.github.caijh.deployer.exception.ClusterNotFoundException;
import com.github.caijh.deployer.exception.KubectlException;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.repository.AppRepository;
import com.github.caijh.deployer.repository.ClusterRepository;
import com.github.caijh.deployer.service.AppService;
import com.github.caijh.deployer.service.ChartService;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class AppServiceImpl implements AppService {

    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private AppRepository appRepository;

    @Inject
    private ChartService chartService;

    @Inject
    private JinjavaConfig jinjavaConfig;

    @Override
    public void create(App app) {
        Cluster cluster = clusterRepository.findById(app.getClusterId()).orElseThrow(ClusterNotFoundException::new);

        App existApp = appRepository.findByClusterIdAndNamespaceAndName(app.getClusterId(), app.getNamespace(), app.getName());

        if (existApp != null) {
            throw new BizException("app name exist");
        }

        Jinjava jinjava = new Jinjava(jinjavaConfig);
        Map<String, Object> context = Maps.newHashMap();
        context.put("name", app.getName());
        context.putAll(app.getValuesJson());

        String chartPath = chartService.findChartPath(app.getChartName(), app.getChartVersion());
        File templatesDir = new File(chartPath + File.separator + "templates");
        File[] templates = templatesDir.listFiles();
        if (templates == null || templates.length <= 0) {
            throw new BizException();
        }

        app.setRevision(1);
        appRepository.save(app);

        try {
            renderTemplateThenWriteFile(app, jinjava, context, templates);

            ProcessResult processResult = Kubectl.apply(cluster, app);
            if (processResult.getExitValue() != 0) {
                throw new KubectlException();
            }
        } catch (Exception e) {
            new File(AppsProperties.appsDir.getPath() + File.separator + app.getName() + File.separator + app.getRevision())
                .deleteOnExit();
            throw new BizException(e);
        }
    }

    private void renderTemplateThenWriteFile(App app, Jinjava jinjava, Map<String, Object> context, File[] templates) throws IOException {
        for (File file : templates) {
            String template = Files.asCharSource(file, UTF_8).read();
            String render = jinjava.render(template, context);
            File targetFile =
                new File(AppsProperties.appsDir.getPath() + File.separator + app.getName()
                    + File.separator + app.getRevision() + File.separator + this.resolveFileName(file));
            Files.createParentDirs(targetFile);
            Files.write(render.getBytes(), targetFile);
        }
    }

    private String resolveFileName(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".j2")) {
            return fileName.substring(0, fileName.lastIndexOf(".j2"));
        }
        return fileName;
    }


}
