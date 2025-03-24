package com.example.bordados.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class ResourceWebConfiguration implements WebMvcConfigurer {

    @Value("${app.upload.dir:${user.dir}/images}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();

        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "file:" + absolutePath + "/",
                        "classpath:/images/"
                )
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}