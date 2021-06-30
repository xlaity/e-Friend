package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tanhua.huanxin")
@Data
public class HuanXinProperties {

    private String url;
    private String orgName;
    private String appName;
    private String clientId;
    private String clientSecret;

    public String getHuanXinUrl() {
        return this.url + "/"
                + this.orgName + "/"
                + this.appName;
    }
}