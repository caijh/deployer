package com.github.caijh.deployer.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.github.caijh.commons.base.exception.BizRuntimeException;
import com.github.caijh.commons.util.process.ProcessResult;
import com.github.caijh.deployer.cmd.CommandReceiver;
import com.github.caijh.deployer.cmd.kubectl.ApplySubCommand;
import com.github.caijh.deployer.cmd.kubectl.DeleteSubCommand;
import com.github.caijh.deployer.config.props.AppsProperties;
import com.github.caijh.deployer.enums.AppStatusEnum;
import com.github.caijh.deployer.exception.AppNotFoundException;
import com.github.caijh.deployer.exception.ClusterNotFoundException;
import com.github.caijh.deployer.exception.KubectlException;
import com.github.caijh.deployer.jinjava.Base64EncodeFilter;
import com.github.caijh.deployer.model.App;
import com.github.caijh.deployer.model.Cluster;
import com.github.caijh.deployer.repository.AppRepository;
import com.github.caijh.deployer.repository.ClusterRepository;
import com.github.caijh.deployer.service.AppService;
import com.github.caijh.deployer.service.ChartService;
import com.github.caijh.deployer.service.ClusterService;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
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

    @Inject
    private ClusterService clusterService;

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

            processResult = CommandReceiver.getInstance().dispatch(new ApplySubCommand(cluster, app));

            if (!processResult.isSuccessful()) {
                throw new KubectlException(processResult.getConsoleString());
            }
        } catch (Exception e) {
            processResult = CommandReceiver.getInstance().dispatch(new DeleteSubCommand(cluster, app));
            if (processResult.isSuccessful()) {
                File toDeleted = new File(AppsProperties.appsDir.getPath() + File.separator + app.getName() + File.separator + app.getRevision());// 清除渲染出来的文件
                FileSystemUtils.deleteRecursively(toDeleted);
            }

            throw new BizRuntimeException(e);
        }
    }

    @Override
    public Page<App> list(Pageable pageable) {
        Page<App> page = appRepository.findAll(pageable);
        page.getContent().forEach(e -> {
            KubernetesClient client = clusterService.getKubernetesClient(e.getClusterId());

            Boolean ready = client.apps().deployments().inNamespace(e.getNamespace()).withName(e.getName()).isReady(); //
            if (ready == null) {
                e.setStatus(AppStatusEnum.UNKNOWN);
            } else {
                e.setStatus(ready ? AppStatusEnum.RUNNING : AppStatusEnum.NOT_READY);
            }
        });
        return page;
    }

    @Override
    public boolean delete(String appId) {
        App app = appRepository.findById(appId).orElseThrow(AppNotFoundException::new);

        Cluster cluster = clusterRepository.findById(app.getClusterId()).orElseThrow(ClusterNotFoundException::new);

        ProcessResult processResult = CommandReceiver.getInstance().dispatch(new DeleteSubCommand(cluster, app));
        if (!processResult.isSuccessful()) {
            throw new BizRuntimeException();
        }

        appRepository.delete(app);

        File toDeleted = new File(AppsProperties.appsDir.getPath() + File.separator + app.getName() + File.separator);
        FileSystemUtils.deleteRecursively(toDeleted);

        return true;
    }

    @Override
    public Optional<App> getById(String appId) {
        return appRepository.findById(appId);
    }

    @Override
    public void logs(String appId, HttpServletResponse response) throws IOException {
        App app = appRepository.findById(appId).orElseThrow(AppNotFoundException::new);

        KubernetesClient kubernetesClient = clusterService.getKubernetesClient(app.getClusterId());

        Pod pod = kubernetesClient.pods().inNamespace(app.getNamespace()).withLabel("app", app.getName()).list().getItems().get(0);
        if (pod != null) {
            kubernetesClient.pods().inNamespace(app.getNamespace())
                            .withName(pod.getMetadata().getName())
                            .tailingLines(20).watchLog(response.getOutputStream());
        }

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
