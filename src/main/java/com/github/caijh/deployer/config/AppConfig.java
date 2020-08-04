package com.github.caijh.deployer.config;

import com.hubspot.jinjava.JinjavaConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JinjavaConfig jinjavaConfig() {
        return JinjavaConfig.newBuilder().withTrimBlocks(true).build();
    }

}
