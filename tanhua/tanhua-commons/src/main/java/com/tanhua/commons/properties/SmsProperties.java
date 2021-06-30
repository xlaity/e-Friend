package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云短信属性类
 */
@Data
@ConfigurationProperties(prefix = "tanhua.sms")
public class SmsProperties {
    // 签名名称
    private String signName;
    // 模版CODE
    private String templateCode;
    // AccessKey ID
    private String accessKey;
    // AccessKey Secret
    private String secret;
}