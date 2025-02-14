package es.upm.miw.betca_tpv_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class}) // Not:resource  /error
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
