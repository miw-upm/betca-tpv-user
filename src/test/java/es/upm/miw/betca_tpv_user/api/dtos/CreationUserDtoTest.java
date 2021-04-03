package es.upm.miw.betca_tpv_user.api.dtos;

import es.upm.miw.betca_tpv_user.data.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreationUserDtoTest {

    @Test
    void testToUser() {
        User user = UserDto.builder().mobile("666666666").firstName("daemon").email("email@gmail.com").build().toUser();
        assertNotNull(user.getRole());
        assertTrue(user.getActive());
    }
}
