package pl.mikus.learning.aot.xml;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.function.Supplier;

@Configuration
@ImportResource("classpath:/app.xml")
class XmlConfiguration {
}

@RequiredArgsConstructor
class XmlLoggingApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private final MessageProducer messageProducer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println(messageProducer.get());
    }
}

class MessageProducer implements Supplier<String> {

    @Override
    public String get() {
        return "Hello form SPRING AOT";
    }
}