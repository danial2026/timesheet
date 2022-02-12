package com.project.timesheet.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class ApplicationConfig {

    @Value("${app-config.clientId:397300109176-efllj9hjd6tmmq7b0pk11mbqpl64fm2e.apps.googleusercontent.com}")
    private String clientId;

    @Value("${server.url:141.11.246.224}")
    private String url;

    @Value("${server.port:8082}")
    private String port;
}