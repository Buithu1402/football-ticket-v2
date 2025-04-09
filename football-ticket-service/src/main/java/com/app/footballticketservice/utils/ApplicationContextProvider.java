package com.app.footballticketservice.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationContextProvider implements ApplicationContextAware {

    static ApplicationContext context;

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return Optional.ofNullable(context)
                .map(ctx -> ctx.getBean(beanName))
                .orElse(null);
    }

    private static void setContext(ApplicationContext context) {
        ApplicationContextProvider.context = context;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        setContext(context);
    }

}
