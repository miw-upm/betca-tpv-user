package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.EmailDto;
import es.upm.miw.betca_tpv_user.api.dtos.RecoverPasswordDto;
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
    void testReadUserProfile() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get().uri(USERS + PROFILE + MOBILE_ID, "6")
                .exchange().expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertEquals("admin", user.getFirstName()));
    }

    @Test
    void testReadUserProfileForbidden() {
        this.restClientTestService.loginCustomer(this.webTestClient)
                .get().uri(USERS + PROFILE + MOBILE_ID, "111111111")
                .exchange().expectStatus().isForbidden();
    }

    @Test
    void testCreateUser() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").email("email0@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserUnauthorized() {
        this.webTestClient
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").email("email@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateUserForbidden() {
        this.restClientTestService.loginManager(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").email("email2@gmail.com").role(Role.ADMIN).build()),
                        UserDto.class)
                .exchange().expectStatus().isForbidden();
    }

    @Test
    void testCreateUserConflict() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666666000").firstName("daemon").email("email3@gmail.com").build()), UserDto.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateFullUser() {
        this.restClientTestService.loginManager(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile("666001666").firstName("daemon").familyName("family")
                        .address("address").password("123").dni("dni").email("email4@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserWithoutNumber() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(USERS)
                .body(Mono.just(UserDto.builder().mobile(null).firstName("kk").email("email5@gmail.com").build()), UserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateCustomer() {
        this.webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(USERS + CUSTOMERS)
                        .build())
                .body(Mono.just(UserDto.builder().mobile("123456787").firstName("Hector").familyName("family")
                        .address("address").password("123").dni("dni").email("email6@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testUpdateUser() {
        WebTestClient webTestClient = this.restClientTestService.loginOperator(this.webTestClient);

        webTestClient
                .put()
                .uri(USERS + MOBILE_ID, "123456788")
                .body(Mono.just(UserDto.builder().mobile("123456788").firstName("Pablo").familyName("family")
                        .address("address").password("6").dni("dni").email("email7@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();

        this.restClientTestService.login("123456788", this.webTestClient)
                .get()
                .uri(USERS + PROFILE + MOBILE_ID, "123456788")
                .exchange().expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertEquals("Pablo", user.getFirstName()));
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

    @Test
    void testSendEmailNotFound() {
        this.webTestClient
                .post().uri(USERS + SEND_EMAIL)
                .body(Mono.just(EmailDto.builder().email("h.mumartin@gmail.com").build()), EmailDto.class)
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void testUpdateUserPassword() {
        this.webTestClient
                .put().uri(USERS + RECOVER)
                .body(Mono.just(RecoverPasswordDto.builder().mail("6@gmail.com").password("1234").build()), RecoverPasswordDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testUpdateUserPasswordNotFound() {
        this.webTestClient
                .put().uri(USERS + RECOVER)
                .body(Mono.just(RecoverPasswordDto.builder().mail("h.mumartin@gmail.com").password("1234").build()), RecoverPasswordDto.class)
                .exchange().expectStatus().isNotFound();
    }

}
