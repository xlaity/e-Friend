package com.tanhua.commons.config;

import com.tanhua.commons.properties.*;
import com.tanhua.commons.templates.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 */
@Configuration
@EnableConfigurationProperties({
        SmsProperties.class,  // 指定某类生成bean
        OssProperties.class,
        AipFaceProperties.class,
        HuanXinProperties.class,
        HuaWeiUGCProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean // 指定方法的返回值生成bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
        return new SmsTemplate(properties);
    }

    @Bean // 指定方法的返回值生成bean
    public OssTemplate ossTemplate(OssProperties properties) {
        return new OssTemplate(properties);
    }

    @Bean // 指定方法的返回值生成bean
    public AipFaceTemplate aipFaceTemplate(AipFaceProperties properties) {
        return new AipFaceTemplate(properties);
    }

    @Bean // 指定方法的返回值生成bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties) {
        return new HuanXinTemplate(properties);
    }

    @Bean // 指定方法的返回值生成bean
    public HuaWeiUGCTemplate huaWeiUGCTemplate(HuaWeiUGCProperties properties) {
        return new HuaWeiUGCTemplate(properties);
    }


}
