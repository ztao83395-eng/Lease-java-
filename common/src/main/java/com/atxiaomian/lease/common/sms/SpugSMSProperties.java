package com.atxiaomian.lease.common.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spug 推送助手短信接口配置。
 */
@Data
@ConfigurationProperties(prefix = "spug.sms")
public class SpugSMSProperties {

    /** Spug 推送助手地址，例如 https://push.spug.cc。 */
    private String endpoint;

    /** Spug 控制台中选择的官方短信模板编码。 */
    private String templateCode;

    /** 带有效时长的模板所需的分钟数；普通模板留空。 */
    private String number;

    private int connectTimeout = 3000;

    private int readTimeout = 5000;
}
