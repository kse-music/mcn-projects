package com.hiekn.boot.autoconfigure.qiniu;

import com.qiniu.storage.UploadManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(UploadManager.class)
@EnableConfigurationProperties(QiNiuProperties.class)
public class QiNiuAutoConfiguration {

    private QiNiuProperties properties;

    public QiNiuAutoConfiguration(QiNiuProperties properties) {
        this.properties = properties;
    }

    @Bean
    public QiNiu qiNiu(){
       return new QiNiu(properties);
    }

}