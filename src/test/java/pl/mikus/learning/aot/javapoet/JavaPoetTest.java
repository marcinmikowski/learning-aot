package pl.mikus.learning.aot.javapoet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.stream.Stream;

@SpringBootTest
@ContextConfiguration(locations = "classpath:/app.xml")
class JavaPoetTest {

    private TypeSpec typeSpec(Class<?> target) {
        Assert.state(!java.lang.reflect.Modifier.isFinal(target.getModifiers()), "Target class can not be final");

        var methods = Stream.of(ReflectionUtils.getUniqueDeclaredMethods(target))
                .filter(method -> !java.lang.reflect.Modifier.isPrivate(method.getModifiers()) &&
                        !ReflectionUtils.isObjectMethod(method))
                .map(this::methodSpec)
                .toList();

        var newType = TypeSpec.classBuilder(target.getSimpleName() + "__Subclass")
                .addModifiers(Modifier.PUBLIC);

        Stream.of(target.getInterfaces()).forEach(newType::addSuperinterface);

        newType.superclass(target);

        methods.forEach(newType::addMethod);

        return newType.build();
    }

    private MethodSpec methodSpec(Method method) {
        var newMethodDefinition = MethodSpec.methodBuilder(method.getName())
                .addModifiers(Modifier.PUBLIC)
                .returns(method.getReturnType())
                .addAnnotation(Override.class);

        var paramNames = Stream.of(method.getParameters())
                .map(parameter -> {
                    var paramName = parameter.getName();
                    newMethodDefinition.addParameter(parameter.getType(), paramName);
                    return paramName;
                }).toList();

        var returnStatement = String.format("%s super.$L($L)",
                method.getReturnType().equals(Void.class) ? "": " return ");

        newMethodDefinition.addStatement(returnStatement,
                method.getName(),
                StringUtils.collectionToCommaDelimitedString(paramNames));

        return newMethodDefinition.build();
    }

    @Test
    void subclass() throws Exception {
        var typeSpec = typeSpec(CustomerService.class);
        var javaFile = JavaFile.builder(CustomerService.class.getPackageName(), typeSpec).build();
        var code = javaFile.toString();
        System.out.println(code);
    }
}

class CustomerService {
    Customer save(String name) { return null; }
    Customer byId(Integer id) { return null; }
}

record Customer(Integer id, String name) {}