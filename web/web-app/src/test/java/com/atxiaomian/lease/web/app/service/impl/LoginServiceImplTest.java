package com.atxiaomian.lease.web.app.service.impl;

import com.atxiaomian.lease.common.exception.LeaseException;
import com.atxiaomian.lease.web.app.service.SmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private SmsService smsService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void getSMSCodeSendsSixDigitCode() {
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        loginService.getSMSCode("13800138000");

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsService).sendCode(eq("13800138000"), codeCaptor.capture());
        assertTrue(codeCaptor.getValue().matches("\\d{6}"));
    }

    @Test
    void getSMSCodeRejectsBlankPhone() {
        LeaseException exception = assertThrows(
                LeaseException.class,
                () -> loginService.getSMSCode(" ")
        );

        assertEquals(502, exception.getCode());
    }
}
