package pl.mikus.learning.aot.properties;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({DemoProperties.class})
class PropertiesConfig {

    @Bean
    ApplicationListener<ApplicationStartedEvent> propertiesApplicationListener(DemoProperties properties) {
        return event -> System.out.println("Readed property is: " + properties.name());
    }
}

@ConfigurationProperties(prefix = "demoprops")
record DemoProperties(String name) {}