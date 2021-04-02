package es.upm.miw.betca_tpv_user.domain.services;

import es.upm.miw.betca_tpv_user.TestConfig;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.data.model.User;
import es.upm.miw.betca_tpv_user.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_user.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class AdminServiceIT {

    @Autowired
    private AdminService adminService;

    @Test
    void testCreate() {
        User user = User.builder().mobile("55").firstName("user1").role(Role.CUSTOMER).build();
        this.adminService.create(user);
        assertThat(this.adminService.readByMobile("55").getMobile(), is("55"));
    }

    @Test
    void testCreateMobileAlreadyExist() {
        User user = User.builder().mobile("6").firstName("user2").role(Role.CUSTOMER).build();
        assertThrows(ConflictException.class, () -> this.adminService.create(user));
    }

    @Test
    void testReadAll() {
        List<User> users = adminService.readAll().collect(Collectors.toList());;

        assertEquals("666666000",users.get(1).getMobile());
        assertEquals("man",users.get(2).getFirstName());
        assertEquals(Role.OPERATOR,users.get(3).getRole());
    }

    @Test
    void testReadByMobile() {
        assertEquals("admin", adminService.readByMobile("6").getFirstName());
        assertNotEquals("admin", adminService.readByMobile("666666001").getFirstName());
        assertEquals("ope@gmail.com", adminService.readByMobile("666666002").getEmail());
    }

    @Test
    void testUpdate() {
        User user = User.builder().mobile("44").firstName("user4").role(Role.OPERATOR).email("user4@gmail.com").build();
        adminService.create(user);
        User newUser = User.builder().mobile("44").firstName("user4").role(Role.CUSTOMER).email("user4@hotmail.com").build();

        adminService.update(user.getMobile(), newUser);
        assertEquals(adminService.readByMobile(user.getMobile()).getRole(), newUser.getRole());
        assertEquals(adminService.readByMobile(user.getMobile()).getFirstName(), newUser.getFirstName());

    }

    @Test
    void testDelete() {
        User user = User.builder().mobile("90").firstName("user5").role(Role.MANAGER).email("user5@gmail.com").build();
        adminService.create(user);
        assertThat(adminService.readByMobile(user.getMobile()).getRole(), is(Role.MANAGER));
        adminService.delete(user);
        assertThrows(NotFoundException.class, () -> this.adminService.readByMobile("90"));


    }
}
