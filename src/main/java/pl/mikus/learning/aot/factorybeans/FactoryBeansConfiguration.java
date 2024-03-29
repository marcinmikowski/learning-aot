package pl.mikus.learning.aot.factorybeans;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FactoryBeansConfiguration {
    @Bean
    AnimalFactoryBean animalFactoryBean() {
        return new AnimalFactoryBean(true, false);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(Animal animal) {
        return event -> System.out.println("Animal speaks : " + animal.speak());
    }

}

class AnimalFactoryBean implements FactoryBean<Animal> {

    private final boolean likesYarn;
    private final boolean likesFrisbees;

    AnimalFactoryBean(boolean likesYarn, boolean likesFrisbees) {
        this.likesYarn = likesYarn;
        this.likesFrisbees = likesFrisbees;
    }

    @Override
    public Animal getObject() throws Exception {
        if (likesYarn && !likesFrisbees) {
            return new Cat();
        }
        return new Dog();
    }

    @Override
    public Class<?> getObjectType() {
        return Animal.class;
    }
}

interface Animal {
    String speak();
}

class Cat implements Animal {

    @Override
    public String speak() {
        return "meow..";
    }
}

class Dog implements Animal {

    @Override
    public String speak() {
        return "woof!";
    }
}
