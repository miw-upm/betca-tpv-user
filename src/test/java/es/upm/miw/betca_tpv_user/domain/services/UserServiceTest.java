package es.upm.miw.betca_tpv_user.domain.services;

import es.upm.miw.betca_tpv_user.TestConfig;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.data.model.User;
import es.upm.miw.betca_tpv_user.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_user.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_user.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestConfig
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserForbidden() {
        User user = User.builder().mobile("666000666").firstName("k").role(Role.ADMIN).build();
        assertThrows(ForbiddenException.class, () -> this.userService.createUser(user, Role.MANAGER));
    }

    @Test
    void testCreateUserMobileAlreadyExist() {
        User user = User.builder().mobile("6").firstName("Hector").role(Role.CUSTOMER).build();
        assertThrows(ConflictException.class, () -> this.userService.createUser(user, Role.ADMIN));
    }

    @Test
    void testCreateUserSuccessfully() {
        User user = User.builder().mobile("649111224").firstName("Hector").role(Role.CUSTOMER).build();
        this.userService.createUser(user, Role.ADMIN);
        assertThat(this.userService.readByMobile("649111224").getMobile(), is("649111224"));
    }

    @Test
    void testReadUserNotFound() {
        assertThrows(NotFoundException.class, () -> this.userService.readByMobile("15"));
    }

    @Test
    void testReadUserSuccessfully() {
        assertThat(this.userService.readByMobile("6").getMobile(), is("6"));
    }

}
