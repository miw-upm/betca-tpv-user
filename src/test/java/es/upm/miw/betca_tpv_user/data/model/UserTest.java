package es.upm.miw.betca_tpv_user.data.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    void testUser(){
        assertThrows(NullPointerException.class, ()-> User.builder().mobile(null).firstName("kk").build());
    }

}
