package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tanhua.huawei")
@Data
public class HuaWeiUGCProperties {

    private String username;
    private String password;
    private String project;
    private String domain;
    private String cagegoriesText;
    private String cagegoriesImage;

}