package com.hiekn.boot.autoconfigure.db;

import com.hiekn.boot.autoconfigure.context.McnPropertiesPostProcessor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultipleDataSourceRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private Environment environment;

    public MultipleDataSourceRegistryPostProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
            String[] dbs = env.getProperty(MultipleMybatisAutoConfiguration.PREFIX + "name", String[].class);
            String basePackage = env.getProperty((McnPropertiesPostProcessor.APP_BASE_PACKAGE_PROPERTY));
            for (String db : dbs) {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(org.apache.tomcat.jdbc.pool.DataSource.class);
                beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                beanDefinition.setSynthetic(true);
                configConnectPool(beanDefinition, db);
                StringBuilder sb = new StringBuilder();
                registry.registerBeanDefinition(sb.append(db).append("DataSource").toString(), beanDefinition);

                try {
                    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
                    factoryBean.setVfs(SpringBootVFS.class);
                    ResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
                    org.apache.ibatis.session.Configuration conf = new org.apache.ibatis.session.Configuration();
                    conf.setMapUnderscoreToCamelCase(true);

                    RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(SqlSessionFactoryBean.class);
                    rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                    Map<String, Object> map = new HashMap<>();
                    map.put("vfs", SpringBootVFS.class);
                    map.put("dataSource", new RuntimeBeanReference(sb.toString()));

                    sb.setLength(0);
                    sb.append("classpath*:mapper/").append(db).append("/*.xml");
                    map.put("mapperLocations", pathResolver.getResources(sb.toString()));

                    sb.setLength(0);
                    sb.append(basePackage).append(".bean");
                    map.put("typeAliasesPackage", sb.toString());

                    sb.setLength(0);
                    sb.append(basePackage).append(".dao.handler");
                    map.put("typeHandlersPackage", sb.toString());

                    map.put("configuration", conf);
                    rootBeanDefinition.getPropertyValues().addPropertyValues(map);

                    sb.setLength(0);
                    sb.append(db).append("SqlSessionFactory");
                    registry.registerBeanDefinition(sb.toString(), rootBeanDefinition);

                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private void configConnectPool(GenericBeanDefinition beanDefinition, String db) {
        String propertyPrefixKey = new StringBuilder(MultipleMybatisAutoConfiguration.PREFIX).append(db).append(".").toString();
        Map<String, Object> map = new HashMap<>();
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(org.apache.tomcat.jdbc.pool.DataSource.class);
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            if (!"class".equals(name)) {
                Iterable<String> relaxedTargetNames = new RelaxedNames(name);
                Iterator<String> iterator = relaxedTargetNames.iterator();
                while (iterator.hasNext()) {
                    String key = propertyPrefixKey + iterator.next();
                    if (environment.containsProperty(key)) {
                        map.put(name, environment.getProperty(key));
                        break;
                    } else {
                        iterator.remove();
                    }
                }
                if (!iterator.hasNext()) {//set default property
                    Iterable<String> names = new RelaxedNames(name);
                    for (String s : names) {
                        String key = "spring.datasource.tomcat." + s;
                        if (environment.containsProperty(key)) {
                            map.put(name, environment.getProperty(key));
                            break;
                        }
                    }
                }
            }
        }
        beanDefinition.getPropertyValues().addPropertyValues(map);
    }

}
