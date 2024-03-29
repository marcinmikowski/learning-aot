package pl.mikus.learning.aot.detection;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NativeDetector;

@Configuration
class DetectionConfiguration {
    @Bean
    ApplicationListener<ApplicationReadyEvent> detectionApplicationListener() {
        return event -> System.out.println("Is NATIVE image? - " + NativeDetector.inNativeImage());
    }
}
