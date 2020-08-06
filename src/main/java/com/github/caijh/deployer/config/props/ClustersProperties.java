package com.github.caijh.deployer.config.props;

import java.io.File;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@ConfigurationProperties(prefix = "clusters")
@Component
@Data
public class ClustersProperties implements InitializingBean {

    private static File clustersDir;
    private String dir;

    public static File getClustersDir() {
        return clustersDir;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        clustersDir = ResourceUtils.getFile(dir);
    }

}
