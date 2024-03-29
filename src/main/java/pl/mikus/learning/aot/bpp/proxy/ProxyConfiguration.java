package pl.mikus.learning.aot.bpp.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@Configuration
class ProxyConfiguration {
    @Bean
    ApplicationListener<ApplicationReadyEvent> proxyApplicationListener(OrderService orderService) {
        return event -> orderService.addToPrice(2.15);
    }

    @Bean
    LoggedBeanPostProcessor loggedBeanPostProcessor() {
        return new LoggedBeanPostProcessor();
    }
}

class LoggedBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    private static ProxyFactory proxyFactory(Object target, Class<?> targetClass) {
        var proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(targetClass);
        proxyFactory.setInterfaces(targetClass.getInterfaces());
        proxyFactory.setProxyTargetClass(true);

        proxyFactory.addAdvice((MethodInterceptor) invocation -> {
            var methodName = invocation.getMethod().getName();
            System.out.println("BEFORE: " + methodName);
            var invocationResult = invocation.getMethod().invoke(target, invocation.getArguments());
            System.out.println("AFTER: " + methodName);
            return invocationResult;
        });

        if (Objects.nonNull(target)) {
            proxyFactory.setTarget(target);
        }

        return proxyFactory;
    }

    private static boolean matches(Class<?> aClass) {
        return Objects.nonNull(aClass) &&
                aClass.isAnnotationPresent(ByProxyLogged.class);
    }

    @Override
    public Class<?> determineBeanType(Class<?> beanClass, String beanName) throws BeansException {
        if (matches((beanClass))) {
            return proxyFactory(null, beanClass).getProxyClass(beanClass.getClassLoader());
        }
        return beanClass;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (matches(bean.getClass())) {
            return proxyFactory(bean, bean.getClass()).getProxy();
        }
        return bean;
    }
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@interface ByProxyLogged {
}

@Service
@ByProxyLogged
class OrderService {
    void addToPrice(double amount) {
        System.out.println("In Order Service added amount: " + amount);
    }
}
