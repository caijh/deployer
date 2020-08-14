package com.github.caijh.deployer.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.github.caijh.commons.base.exception.BizRuntimeException;
import com.github.caijh.deployer.cmd.Kubectl;
import com.github.caijh.deployer.cmd.ProcessResult;
import com.github.caijh.deployer.config.props.AppsProperties;
import com.github.caijh.deployer.exception.ClusterNotFoundException;
import com.github.caijh.deployer.exception.KubectlException;
import com.github.caijh.deployer.jinjava.Base64EncodeFilter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

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

    @Transactional
    @Override
    public void create(App app) {
        // 集群信息是否存在
        Cluster cluster = clusterRepository.findById(app.getClusterId()).orElseThrow(ClusterNotFoundException::new);

        // 是否存在同名的app
        App existApp = appRepository.findByClusterIdAndNamespaceAndName(app.getClusterId(), app.getNamespace(), app.getName());
        if (existApp != null) {
            throw new BizRuntimeException("app name exist");
        }

        app.setRevision(1); // 记录app的版本为1
        appRepository.save(app);

        ProcessResult processResult;
        try {
            // 渲染模板并写入到app的revision目录下
            renderTemplateThenWriteFile(app);

            processResult = Kubectl.process(Kubectl.SubCommand.APPLY, cluster, app);
            if (processResult.getExitValue() != 0) {
                throw new KubectlException();
            }
        } catch (Exception e) {
            processResult = Kubectl.process(Kubectl.SubCommand.DELETE, cluster, app);
            if (processResult.isSuccessful()) {
                File toDeleted = new File(AppsProperties.appsDir.getPath() + File.separator + app.getName() + File.separator + app.getRevision());// 清除渲染出来的文件
                FileSystemUtils.deleteRecursively(toDeleted);
            }

            throw new BizRuntimeException(e);
        }
    }

    @Override
    public Page<App> list(Pageable pageable) {
        return appRepository.findAll(pageable);
    }

    @Override
    public Optional<App> getByName(String clusterId, String appName) {
        return appRepository.findByClusterIdAndName(clusterId, appName);
    }

    private void renderTemplateThenWriteFile(App app) throws IOException {
        Jinjava jinjava = new Jinjava(jinjavaConfig);
        jinjava.getGlobalContext().registerFilter(Base64EncodeFilter.getInstance());
        Map<String, Object> context = Maps.newHashMap();
        context.put("name", app.getName());
        context.putAll(app.getValuesJson());

        String chartPath = chartService.findChartPath(app.getChartName(), app.getChartVersion());
        File templatesDir = new File(chartPath + File.separator + "templates");
        File[] templates = templatesDir.listFiles();
        if (templates == null || templates.length <= 0) {
            throw new BizRuntimeException();
        }

        for (File file : templates) {
            String template = Files.asCharSource(file, UTF_8).read();
            String render = jinjava.render(template, context);
            File targetFile = new File(AppsProperties.appsDir.getPath() + File.separator + app.getName()
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
