package pl.mikus.learning.aot.basics;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Stream;

@Configuration
class BasicsConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> basicsApplicationListener(CustomerRepository repository) {
        List<Customer> customerList = Stream.of("A", "B", "C").map(name -> new Customer(null, name)).toList();

        return (event) -> {
            repository.saveAll(customerList).forEach(System.out::println);
            System.out.println("Event: " + event);
        };
    }

}

record Customer(@Id Integer id, String name) {}

interface CustomerRepository extends CrudRepository<Customer, Integer> {}
