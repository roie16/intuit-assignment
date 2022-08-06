package io.intuitdemo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Intuit home assigment", version = "0.1", description = "Intuit home assigment", contact = @Contact(name = "Roie Beck")))
public class IntuitDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntuitDemoApplication.class, args);
    }

}
