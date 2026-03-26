package com.efe.requestmanagementapi.config;

import com.efe.requestmanagementapi.enums.RequestPriority;
import com.efe.requestmanagementapi.enums.RequestStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, RequestPriority.class, RequestPriority::fromValue);
        registry.addConverter(String.class, RequestStatus.class, RequestStatus::fromValue);
    }
}