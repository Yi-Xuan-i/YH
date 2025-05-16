package com.yixuan.yh.admin.init;

import com.yixuan.yh.admin.annotations.RequiresPermission;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class PermissionInitializer implements ApplicationContextAware {

    private final Set<String> allPermissionValues = new HashSet<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取所有带有@RestController注解的bean
        Map<String, Object> restControllers = applicationContext.getBeansWithAnnotation(RestController.class);

        for (Object bean : restControllers.values()) {
            // 获取bean的Class对象
            Class<?> beanClass = AopUtils.getTargetClass(bean);

            // 检查类级别的方法
            processClassAnnotations(beanClass);

            // 检查所有方法级别的注解
            for (Method method : beanClass.getDeclaredMethods()) {
                RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
                if (annotation != null) {
                    Collections.addAll(allPermissionValues, annotation.value());
                }
            }
        }
    }

    private void processClassAnnotations(Class<?> beanClass) {
        RequiresPermission classAnnotation = beanClass.getAnnotation(RequiresPermission.class);
        if (classAnnotation != null) {
            Collections.addAll(allPermissionValues, classAnnotation.value());
        }
    }

    public Set<String> getAllPermissionValues() {
        return Collections.unmodifiableSet(allPermissionValues);
    }
}