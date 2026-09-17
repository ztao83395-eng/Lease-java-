package com.atxiaomian.lease.web.app.service.impl;

import com.atxiaomian.lease.common.exception.LeaseException;
import com.atxiaomian.lease.common.result.ResultCodeEnum;
import com.atxiaomian.lease.common.sms.SpugSMSProperties;
import com.atxiaomian.lease.web.app.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final RestTemplate restTemplate;
    private final SpugSMSProperties properties;

    public SmsServiceImpl(
            @Qualifier("spugRestTemplate") RestTemplate restTemplate,
            SpugSMSProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void sendCode(String phone, String verifyCode) {
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(verifyCode)) {
            throw serviceError("短信手机号或验证码为空");
        }
        if (properties == null
                || !StringUtils.hasText(properties.getEndpoint())
                || !StringUtils.hasText(properties.getTemplateCode())) {
            throw serviceError("Spug短信服务未配置，请填写 spug.sms.endpoint 和 spug.sms.template-code");
        }

        try {
            URI uri = UriComponentsBuilder.fromUriString(properties.getEndpoint().trim())
                    .pathSegment("send", properties.getTemplateCode().trim())
                    .build()
                    .toUri();
            Map<String, String> requestBody = new LinkedHashMap<>();
            requestBody.put("targets", phone);
            requestBody.put("code", verifyCode);
            if (StringUtils.hasText(properties.getNumber())) {
                requestBody.put("number", properties.getNumber().trim());
            }
            ResponseEntity<Map> response = restTemplate.postForEntity(uri, requestBody, Map.class);
            Map<?, ?> body = response.getBody();
            Integer code = readInteger(body == null ? null : body.get("code"));
            String message = readString(body == null ? null : body.get("msg"));
            if (!response.getStatusCode().is2xxSuccessful() || !Integer.valueOf(200).equals(code)) {
                log.warn("Spug SMS rejected request: httpStatus={}, code={}, message={}",
                        response.getStatusCode().value(), code, message);
                throw serviceError(StringUtils.hasText(message) ? "短信发送失败：" + message : "短信发送失败");
            }
            log.debug("Spug SMS request accepted: requestId={}",
                    readString(body == null ? null : body.get("request_id")));
        } catch (LeaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Spug SMS request failed", e);
            throw serviceError("短信服务请求失败，请检查 Spug 配置和网络连接");
        }
    }

    private LeaseException serviceError(String message) {
        return new LeaseException(ResultCodeEnum.SERVICE_ERROR.getCode(), message);
    }

    private Integer readInteger(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    private String readString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
