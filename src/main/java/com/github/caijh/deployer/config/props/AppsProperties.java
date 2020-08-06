package com.github.caijh.deployer.config.props;

import java.io.File;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@ConfigurationProperties(prefix = "apps")
@Component
@Data
public class AppsProperties implements InitializingBean {

    public static File appsDir;
    private String dir;

    @Override
    public void afterPropertiesSet() throws Exception {
        appsDir = ResourceUtils.getFile(dir);
    }

}
