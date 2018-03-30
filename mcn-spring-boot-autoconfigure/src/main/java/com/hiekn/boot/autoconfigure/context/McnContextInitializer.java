package com.hiekn.boot.autoconfigure.context;

import com.hiekn.boot.autoconfigure.web.util.SpringBeanUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

@Order(McnApplicationListener.DEFAULT_ORDER)
public class McnContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        configurableApplicationContext.addApplicationListener(new SpringBeanUtilsListener());
        configurableApplicationContext.addBeanFactoryPostProcessor(new McnBeanFactoryRegistryPostProcessor());

    }

    private static class SpringBeanUtilsListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            SpringBeanUtils.setApplicationContext(event.getApplicationContext());
        }

    }

}
