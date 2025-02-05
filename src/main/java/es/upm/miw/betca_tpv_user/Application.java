package es.upm.miw.betca_tpv_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class}) // Not: /error
public class Application {

    public static void main(String[] args) { // mvn clean spring-boot:run
        System.out.println("DATABASE_URL: " + System.getenv("DATABASE_URL"));
        System.out.println("DATABASE_USERNAME: " + System.getenv("DATABASE_USERNAME"));
        System.out.println("DATABASE_PASSWORD: " + System.getenv("DATABASE_PASSWORD"));
        System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
        SpringApplication.run(Application.class, args);
    }

}
