package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_user.api.resources.UserResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTestConfig
class UserResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testLogin() {
        this.restClientTestService.loginAdmin(this.webTestClient);
        assertTrue(this.restClientTestService.getToken().length() > 10);
    }

    @Test
    void testReadUser() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get().uri(USERS + MOBILE_ID, "666666003")
                .exchange().expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertEquals("c1", user.getFirstName()));
    }

    @Test
    void testReadUserNotFound() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get().uri(USERS + MOBILE_ID, "999666999")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void testReadUserForbidden() {
        this.restClientTestService.loginCustomer(this.webTestClient)
                .get().uri(USERS + MOBILE_ID, "999666999")
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateUserWithAdmin() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserWithOperator() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000667").firstName("daemon").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserUnauthorizedNoLogin() {
        this.webTestClient
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").build()), UserDto.class)
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateUserUnauthorizedWithCustomer() {
        this.restClientTestService.loginCustomer(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").role(Role.CUSTOMER).build()),
                        UserDto.class)
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateAdminUserForbidden() {
        this.restClientTestService.loginManager(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").role(Role.ADMIN).build()),
                        UserDto.class)
                .exchange().expectStatus().isForbidden();
    }

    @Test
    void testCreateUserConflict() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666666000").firstName("daemon").build()), UserDto.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateFullUser() {
        this.restClientTestService.loginManager(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666001666").firstName("daemon").familyName("family")
                        .address("address").password("123").dni("dni").email("email@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserBadNumber() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("6").firstName("kk").build()), UserDto.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateUserWithoutNumber() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile(null).firstName("kk").build()), UserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testReadAllOperator() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .get().uri(USERS)
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().noneMatch(user -> "admin".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "man".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "ope".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "c1".equals(user.getFirstName()))));
    }

    @Test
    void testReadAllCustomer() {
        this.restClientTestService.loginCustomer(this.webTestClient)
                .get().uri(USERS)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testSearch() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(USERS + SEARCH)
                        .queryParam("mobile", "6")
                        .queryParam("firstName", "c")
                        .queryParam("dni", "e").build())
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().anyMatch(user -> "c1".equals(user.getFirstName()))));
    }

}
