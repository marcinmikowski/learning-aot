package pl.mikus.learning.aot.bfpp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

import static pl.mikus.learning.aot.bfpp.BfppConfiguration.BEAN_NAME;

@Configuration
class BfppConfiguration {
    static final String BEAN_NAME = "myBfppListener";

    @Bean
    static BfppBeanFactoryPostProcessorListener bfppBeanFactoryPostProcessorListener() {
        return new BfppBeanFactoryPostProcessorListener();
    }

    @Bean
    static BeanFactoryInitializationAotProcessorListener beanFactoryInitializationAotProcessorListener() {
        return new BeanFactoryInitializationAotProcessorListener();
    }
}

class BeanFactoryInitializationAotProcessorListener implements BeanFactoryInitializationAotProcessor {
    @Override
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {

        if(beanFactory.containsBeanDefinition(BEAN_NAME)) {
            return (ctx, code) -> {
                var hints = ctx.getRuntimeHints();
                hints.reflection().registerType(Product.class, MemberCategory.values());
            };
        }
        return null;
    }
}

class BfppBeanFactoryPostProcessorListener implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!registry.containsBeanDefinition(BEAN_NAME)) {
           registry.registerBeanDefinition(BEAN_NAME,
                   BeanDefinitionBuilder.rootBeanDefinition("pl.mikus.learning.aot.bfpp.BfppApplicationListener")
                           .getBeanDefinition());
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BfppApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        var products = List.of(new Product(UUID.randomUUID().toString()),
                new Product(UUID.randomUUID().toString()),
                new Product(UUID.randomUUID().toString()),
                new Product(UUID.randomUUID().toString()),
                new Product(UUID.randomUUID().toString()));

        for (var p : products) {
            System.out.println(objectMapper.writeValueAsString(p));
        }
    }
}

record Product(String sku) {}