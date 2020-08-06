package com.github.caijh.deployer.config.props;

import java.io.File;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;


@ConfigurationProperties(prefix = "helm.charts")
@Component
@Data
public class ChartsProperties implements InitializingBean {

    public static File chartsDir;
    /**
     * charts的文件路径.
     */
    private String dir;

    @Override
    public void afterPropertiesSet() throws Exception {
        chartsDir = ResourceUtils.getFile(dir);
    }

}
