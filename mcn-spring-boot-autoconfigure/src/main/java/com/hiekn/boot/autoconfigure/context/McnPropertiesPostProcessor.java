package com.hiekn.boot.autoconfigure.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class McnPropertiesPostProcessor implements EnvironmentPostProcessor,Ordered {

    public static final String APP_BASE_PACKAGE_PROPERTY = "jersey.swagger.base-package";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        //add map config
        Map<String, Object> mapProp = new HashMap<>();
        Class<?> mainApplicationClass = application.getMainApplicationClass();//maybe is null
        if(Objects.nonNull(mainApplicationClass)){
            mapProp.put(APP_BASE_PACKAGE_PROPERTY,ClassUtils.getPackageName(mainApplicationClass));
        }

        String profiles = environment.getProperty(ConfigFileApplicationListener.ACTIVE_PROFILES_PROPERTY);
        String logFileName = environment.getProperty("mcn.log.errorFileName","error");
        if(StringUtils.hasText(profiles)){
            logFileName += "-" + StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles))[0];
        }
        mapProp.put("mcn.log.errorFileName",logFileName);

        mapProp.put("mcn.version",this.getClass().getPackage().getImplementationVersion());
        mapProp.put("logging.level."+mapProp.get(APP_BASE_PACKAGE_PROPERTY)+".dao","info");//do not println query statement

        propertySources.addLast(new MapPropertySource("mcn-map",mapProp));

        try {
            //add global unique config file
            propertySources.addLast(new ResourcePropertySource("mcn-global-unique","classpath:config/mcn.properties"));
        } catch (IOException e) {
            //ignore file not found
        }

        try{
            //add global config file diff environment
            String[] activeProfiles = environment.getActiveProfiles();
            StringBuilder globalConfigName = new StringBuilder(ResourceUtils.CLASSPATH_URL_PREFIX).append("mcn-global");
            if(Objects.nonNull(activeProfiles) && activeProfiles.length > 0){
                globalConfigName.append("-").append(activeProfiles[0]);
            }
            globalConfigName.append(".properties");
            propertySources.addLast(new ResourcePropertySource("mcn-global",globalConfigName.toString()));
        } catch (IOException e) {
            //ignore file not found
        }

        try{
            //add mcn default config can't override
            String path = this.getClass().getResource("").getPath();
            path = path.replaceFirst(ResourceUtils.FILE_URL_PREFIX, ResourceUtils.JAR_URL_PREFIX+ResourceUtils.FILE_URL_PREFIX);
            path = path.replace(ClassUtils.getPackageName(this.getClass()).replace(".", "/"), "META-INF");
            propertySources.addLast(new PropertiesPropertySource("mcn-default",PropertiesLoaderUtils.loadProperties(new UrlResource(path+"mcn-default.properties"))));
        } catch (IOException e) {
            //ignore file not found
        }

    }

    @Override
    public int getOrder() {
        return McnApplicationListener.DEFAULT_ORDER + 1;
    }

}
