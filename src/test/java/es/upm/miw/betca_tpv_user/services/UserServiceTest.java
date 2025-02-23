package es.upm.miw.betca_tpv_user.services;

import es.upm.miw.betca_tpv_user.TestConfig;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.data.model.User;
import es.upm.miw.betca_tpv_user.services.exceptions.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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
    void testFindUsersNotInList() {
        List<String> userMobiles = List.of("666666000", "666666001");
        List<User> usersNotInList = this.userService.findUsersNotInList(userMobiles)
                .toList();

        assertTrue(usersNotInList.stream().noneMatch(user -> userMobiles.contains(user.getMobile())));
        assertTrue(usersNotInList.stream().anyMatch(user -> user.getMobile().equals("666666002")));
        assertTrue(usersNotInList.stream().anyMatch(user -> user.getMobile().equals("666666003")));
        assertTrue(usersNotInList.stream().anyMatch(user -> user.getMobile().equals("666666004")));
        assertTrue(usersNotInList.stream().anyMatch(user -> user.getMobile().equals("666666005")));
        assertTrue(usersNotInList.stream().anyMatch(user -> user.getMobile().equals("66")));
    }
}
