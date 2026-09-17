package com.atxiaomian.lease.web.app.service.impl;

import com.atxiaomian.lease.common.exception.GlobalExceptionHandler;
import com.atxiaomian.lease.common.mybatisplus.MybatisPlusConfiguration;
import com.atxiaomian.lease.common.exception.LeaseException;
import com.atxiaomian.lease.common.sms.SpugSMSConfiguration;
import com.atxiaomian.lease.common.sms.SpugSMSProperties;
import com.atxiaomian.lease.web.app.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spug.sms.endpoint=https://push.spug.cc",
        "spug.sms.template-code=SMS_TEST"
})
class SmsServiceImplTest {

    @Autowired
    private SmsService service;

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean(name = "spugRestTemplate")
    private RestTemplate restTemplate;

    @Test
    void sendCode() {
        assertAll(
                () -> assertNotNull(applicationContext.getBean(SpugSMSConfiguration.class)),
                () -> assertNotNull(applicationContext.getBean(MybatisPlusConfiguration.class)),
                () -> assertNotNull(applicationContext.getBean(MybatisPlusInterceptor.class)),
                () -> assertNotNull(applicationContext.getBean(GlobalExceptionHandler.class)),
                () -> assertSame(restTemplate, applicationContext.getBean("spugRestTemplate", RestTemplate.class))
        );

        when(restTemplate.postForEntity(any(URI.class), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("code", 200, "msg", "请求成功", "request_id", "request-1")));

        service.sendCode("00000000000", "1234");

        verify(restTemplate).postForEntity(
                eq(URI.create("https://push.spug.cc/send/SMS_TEST")),
                eq(Map.of("targets", "00000000000", "code", "1234", "number", "3")),
                eq(Map.class));
    }

    @Test
    void sendCodeReportsSpugBusinessFailure() {
        when(restTemplate.postForEntity(any(URI.class), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.badRequest().body(Map.of("code", 400, "msg", "模板编码无效")));

        LeaseException exception = assertThrows(
                LeaseException.class,
                () -> service.sendCode("00000000000", "1234")
        );

        assertAll(
                () -> assertEquals(203, exception.getCode()),
                () -> assertEquals("短信发送失败：模板编码无效", exception.getMessage())
        );
    }

    @Test
    void sendCodeDoesNotCallSpugWhenConfigurationIsMissing() {
        SmsServiceImpl unconfiguredService = new SmsServiceImpl(restTemplate, new SpugSMSProperties());

        LeaseException exception = assertThrows(
                LeaseException.class,
                () -> unconfiguredService.sendCode("00000000000", "1234")
        );

        assertEquals(203, exception.getCode());
        verifyNoInteractions(restTemplate);
    }
}
