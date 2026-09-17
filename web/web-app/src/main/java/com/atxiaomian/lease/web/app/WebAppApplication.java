package com.atxiaomian.lease.web.app;

import com.atxiaomian.lease.common.exception.GlobalExceptionHandler;
import com.atxiaomian.lease.common.mybatisplus.MybatisPlusConfiguration;
import com.atxiaomian.lease.common.sms.SpugSMSConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.atxiaomian.lease")
@Import({MybatisPlusConfiguration.class, SpugSMSConfiguration.class, GlobalExceptionHandler.class})
public class WebAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAppApplication.class, args);
    }
}
