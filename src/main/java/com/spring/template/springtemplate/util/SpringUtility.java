package com.spring.template.springtemplate.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author Ali Mojahed on 6/4/2021
 * @project spring-template
 **/

@Service
public class SpringUtility implements ApplicationContextAware {
    private static ApplicationContext context;

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBean(String name, Class<T> beanClass) {
        return context.getBean(name, beanClass);
    }

    public static <T> T getProperty(String property, Class<T> propertyType) {
        return context.getEnvironment().getProperty(property, propertyType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
