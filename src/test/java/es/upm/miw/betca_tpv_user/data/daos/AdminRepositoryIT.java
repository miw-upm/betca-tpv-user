package es.upm.miw.betca_tpv_user.data.daos;

import es.upm.miw.betca_tpv_user.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class AdminRepositoryIT {

    @Autowired
    AdminRepository adminRepository;

    @Test
    void testFindByMobile() {
        assertTrue(this.adminRepository.findByMobile("6").isPresent());
    }


}
