package com.easylive.resource.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(value = "resourceWebConfig")
public class WebConfig implements WebMvcConfigurer {

    @Value("${easylive.file.base-path}")
    private String fileBasePath;

    @Value("${easylive.hls.output-path}")
    private String hlsOutputPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileBasePath + "/");

        registry.addResourceHandler("/hls/**")
                .addResourceLocations("file:" + hlsOutputPath + "/");
    }


}