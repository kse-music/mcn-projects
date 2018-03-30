package com.hiekn.boot.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(JWT.class)
@EnableConfigurationProperties({JwtProperties.class})
public class JwtTokenAutoConfiguration {

    private JwtProperties jwtProperties;

    public JwtTokenAutoConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name={"jwtToken"})
    public JwtToken jwtToken() {
        return new JwtToken(jwtProperties);
    }

}
