package pl.mikus.learning.aot.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

@Configuration
@RegisterReflectionForBinding(Person.class)
@ImportRuntimeHints(MigrationRuntimeRegistrar.class)
@RequiredArgsConstructor
class MigrationConfiguration {

    private final ObjectMapper objectMapper;

    @Bean
    ApplicationListener<ApplicationReadyEvent> peopleListener(@Value("classpath:/data.csv") Resource csv) {
        return event -> {
            try (var in = new InputStreamReader(csv.getInputStream())) {
                var data = FileCopyUtils.copyToString(in);
                Stream.of(data.split(System.lineSeparator()))
                        .map(line -> line.split(","))
                        .map(values -> new Person(values[0], values[1]))
                        .map(person -> json(person, objectMapper))
                        .forEach(System.out::println);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }

    @SneakyThrows
    private String json(Person person, ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(person);
    }
}

record Person(String id, String name) {}


class MigrationRuntimeRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        //hints.reflection().registerType(Person.class, MemberCategory.values());
        hints.resources().registerResource(new ClassPathResource("data.csv"));
    }
}