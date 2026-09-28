package com.lsfashioncloset.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    @Value("${app.frontend-url}")
    private String frontendUrl;

    public String frontendUrl() {
        return frontendUrl;
    }
}
