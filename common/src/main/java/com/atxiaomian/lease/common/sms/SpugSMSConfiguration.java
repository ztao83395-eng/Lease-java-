package com.atxiaomian.lease.common.sms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(SpugSMSProperties.class)
public class SpugSMSConfiguration {

    @Bean("spugRestTemplate")
    @ConditionalOnMissingBean(name = "spugRestTemplate")
    public RestTemplate spugRestTemplate(RestTemplateBuilder builder, SpugSMSProperties properties) {
        return builder
                .setConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
                .setReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
                .build();
    }
}
