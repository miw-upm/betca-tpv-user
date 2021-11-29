package es.upm.miw.betca_tpv_user.api.dtos;

import es.upm.miw.betca_tpv_user.data.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    @Test
    void testUserDtoUser() {
        UserDto x = new UserDto(User.builder().mobile("6").firstName("fi").familyName("fa").address("a").email("e")
                .dni("d").password("1").build());
        assertEquals("6", x.getMobile());
        assertEquals("secret", x.getPassword());
    }
}
