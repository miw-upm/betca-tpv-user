package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_user.api.resources.AdminResource.ADMIN;
import static es.upm.miw.betca_tpv_user.api.resources.AdminResource.ROLE;
import static es.upm.miw.betca_tpv_user.api.resources.UserResource.MOBILE_ID;
import static org.junit.jupiter.api.Assertions.*;

@ApiTestConfig
class AdminResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreateByAdmin() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(ADMIN + ROLE, "ADMIN")
                .body(Mono.just(UserDto.builder().mobile("22").firstName("user3").familyName("user")
                        .address("address").password("1234").dni("1234567").email("user@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();

    }

    @Test
    void testCreateUnauthorized() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .post().uri(ADMIN + ROLE, "OPERATOR")
                .body(Mono.just(UserDto.builder().mobile("111").firstName("user2").familyName("user")
                        .address("address").password("1234").dni("1234567").email("user@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testReadAllByCustomer() {
        this.restClientTestService.loginCustomer(this.webTestClient)
                .get().uri(ADMIN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testReadAllByOperator() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .get().uri(ADMIN)
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().anyMatch(user -> "admin".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "66666602K".equals(user.getDni()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "c2@gmail.com".equals(user.getDni()))));
    }

    @Test
    void testReadAllByAdmin() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get().uri(ADMIN)
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().anyMatch(user -> "ope".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "customer".equals(user.getDni()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "c1".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "Hector".equals(user.getFirstName()))));

    }

    @Test
    void testReadAllByManager() {
        this.restClientTestService.loginManager(this.webTestClient)
                .get().uri(ADMIN)
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().anyMatch(user -> "ope".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "man".equals(user.getDni()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "666666000".equals(user.getMobile()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "c1".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "Hector".equals(user.getFirstName()))));

    }

    @Test
    void updateUserByAdmin() {

        this.restClientTestService.loginAdmin(this.webTestClient)
                .put()
                .uri(ADMIN + MOBILE_ID, "66")
                .body(Mono.just(UserDto.builder().mobile("66").firstName("User2").familyName("user")
                        .address("address").password("user2user").dni("000000").email("user2@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();

        this.restClientTestService.loginAdmin(this.webTestClient)
                .get()
                .uri(ADMIN + MOBILE_ID, "66")
                .exchange().expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertEquals("User2", user.getFirstName()));
    }

    @Test
    void updateUserUnauthorized() {
        String pass = new BCryptPasswordEncoder().encode("6");

        this.webTestClient
                .put()
                .uri(ADMIN + MOBILE_ID, "666666001")
                .body(Mono.just(UserDto.builder().mobile("666666001").firstName("man").familyName("user")
                        .address("C/TPV, 12").password(pass).dni("66666601C").email("man@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isUnauthorized();

    }

    @Test
    void readUserByMobileUnauthorized() {
        this.webTestClient
                .get()
                .uri(ADMIN + MOBILE_ID, "6")
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void readUserByMobile() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get()
                .uri(ADMIN + MOBILE_ID, "666666001")
                .exchange().expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertEquals("man", user.getFirstName()));


    }


    @Test
    void testCreateAndDelete() {

        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(ADMIN + ROLE, "ADMIN")
                .body(Mono.just(UserDto.builder().mobile("333").firstName("user3").familyName("user")
                        .address("address").password("1234").dni("1234567").email("user3@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();

        this.restClientTestService.loginAdmin(this.webTestClient)
                .delete().uri(ADMIN + MOBILE_ID, "333")
                .exchange().expectStatus().isOk();

        this.restClientTestService.loginAdmin(this.webTestClient)
                .get()
                .uri(ADMIN + MOBILE_ID, "333")
                .exchange().expectStatus().isNotFound();


    }
}
