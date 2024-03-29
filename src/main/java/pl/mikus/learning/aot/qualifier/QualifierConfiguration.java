package pl.mikus.learning.aot.qualifier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

@Configuration
class QualifierConfiguration {
    @Bean
    ApplicationListener<ApplicationReadyEvent> ios(@Apple MobileMarket mobileMarket) {
        return event -> System.out.println(mobileMarket.getClass());
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> android(@Android MobileMarket mobileMarket) {
        return event -> System.out.println(mobileMarket.getClass());
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> markets(Map<String, MobileMarket> markets) {
        return event -> markets.forEach((k,v) -> System.out.println(k + "=" + v.getClass()));
    }
}

interface MobileMarket {}

@Retention(RetentionPolicy.RUNTIME)
@Qualifier("ios")
@interface Apple {}

@Retention(RetentionPolicy.RUNTIME)
@Qualifier("android")
@interface  Android {}
@Component
@Apple
class AppStore implements MobileMarket {}

@Component
@Android
class PlayStore implements MobileMarket {}
