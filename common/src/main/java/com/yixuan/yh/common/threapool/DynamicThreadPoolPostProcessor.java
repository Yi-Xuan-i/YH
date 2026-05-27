package com.yixuan.yh.common.threapool;

import com.yixuan.yh.common.properties.DynamicThreadPoolProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DynamicThreadPoolPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private Environment environment;
    private DynamicThreadPoolRegistry registry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.environment = applicationContext.getEnvironment();
        this.registry = applicationContext.getBean(DynamicThreadPoolRegistry.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 【核心点】此时常规的 @ConfigurationProperties 还没生效，我们需要用 Spring 的 Binder 手动绑定配置
        BindResult<DynamicThreadPoolProperties> bindResult = Binder.get(environment)
                .bind("dynamic", DynamicThreadPoolProperties.class);

        if (!bindResult.isBound()) {
            return;
        }

        DynamicThreadPoolProperties properties = bindResult.get();

        // 遍历配置，动态注册 BeanDefinition
        properties.getThreadpools().forEach((poolName, params) -> {

            // 构建一个 ThreadPoolExecutor 的 Bean 定义
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolExecutor.class);

            // 传入构造函数参数（必须和 ThreadPoolExecutor 的构造方法参数顺序一致）
            builder.addConstructorArgValue(params.getCorePoolSize());
            builder.addConstructorArgValue(params.getMaximumPoolSize());
            builder.addConstructorArgValue(params.getKeepAliveTime());
            builder.addConstructorArgValue(TimeUnit.SECONDS);

            // 传入队列
            builder.addConstructorArgValue(new LinkedBlockingQueue<>(params.getQueueCapacity()));

            // 注册到 Spring 容器中
            registry.registerBeanDefinition(poolName, builder.getBeanDefinition());
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BindResult<DynamicThreadPoolProperties> bindResult = Binder.get(environment)
                .bind("dynamic", DynamicThreadPoolProperties.class);

        if (!bindResult.isBound()) {
            return;
        }

        DynamicThreadPoolProperties properties = bindResult.get();

        properties.getThreadpools().forEach((poolName, params) -> {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) beanFactory.getBean(poolName);
            this.registry.register(poolName, executor);
        });
    }
}