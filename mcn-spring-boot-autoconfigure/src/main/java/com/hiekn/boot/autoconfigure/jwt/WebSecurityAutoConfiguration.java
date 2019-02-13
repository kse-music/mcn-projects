package com.hiekn.boot.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiekn.boot.autoconfigure.base.util.McnUtils;
import com.hiekn.boot.autoconfigure.web.exception.handler.JwtAuthenticationEntryPoint;
import com.hiekn.boot.autoconfigure.web.filter.JwtAuthenticationTokenFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import java.util.*;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ AbstractSecurityWebApplicationInitializer.class, SessionCreationPolicy.class,JWT.class })
@ConditionalOnProperty(prefix = "jwt.security", name = "login", havingValue = "true")
@EnableConfigurationProperties
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private Environment environment;
    private JwtProperties jwtProperties;

    public WebSecurityAutoConfiguration( Environment environment,JwtProperties jwtProperties) {
        this.environment = environment;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        boolean filterCross = environment.getProperty("filter.cross", Boolean.class, true);
        if(filterCross){
            http.cors();
        }
        http
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint())
        .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .anyRequest().authenticated()
        .and()
            .addFilterBefore(new JwtAuthenticationTokenFilter(authenticationEntryPoint()),UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        String basePath = McnUtils.parsePath(environment.getProperty("spring.jersey.application-path"));
        List<String> defaultIgnoreUrls = new ArrayList<>(Arrays.asList(basePath+"/swagger.json",basePath+"/Swagger.html","/Swagger/**"));
        List<String> ignoreUrls = jwtProperties.getSecurity().getIgnoreUrls();
        if(Objects.nonNull(ignoreUrls) && !ignoreUrls.isEmpty()){
            for (String ignoreUrl : ignoreUrls) {
                defaultIgnoreUrls.add(basePath+McnUtils.parsePath(ignoreUrl));
            }
        }
        web.ignoring().antMatchers(defaultIgnoreUrls.toArray(new String[defaultIgnoreUrls.size()]));
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtProperties jwtProperties(){
        return new JwtProperties();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new JwtAuthenticationEntryPoint(new ObjectMapper());
    }

}