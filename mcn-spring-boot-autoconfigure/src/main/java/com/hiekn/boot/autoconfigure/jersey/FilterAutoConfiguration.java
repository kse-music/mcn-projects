package com.hiekn.boot.autoconfigure.jersey;

import com.hiekn.boot.autoconfigure.web.filter.CheckCertificateFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * uniform register some filter
 *
 * @author DingHao
 * @date 2019/1/9 11:31
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(FilterProperties.class)
public class FilterAutoConfiguration {

    private FilterProperties filterProperties;

    public FilterAutoConfiguration(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.cors.CorsConfigurationSource")
    @ConditionalOnProperty(prefix = "filter", name = {"cross"}, havingValue = "true", matchIfMissing = true)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    @ConditionalOnClass(name = "com.hiekn.licence.verify.VerifyLicense")
    public FilterRegistrationBean checkCertificateFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CheckCertificateFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

//    @Bean
//    @ConditionalOnClass(name = "org.jsoup.Jsoup")
//    public FilterRegistrationBean xssFilter() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new XssFilter(filterProperties));
//        filterRegistrationBean.setOrder(2);
//        filterRegistrationBean.addUrlPatterns("/*");
//        return filterRegistrationBean;
//    }

}
