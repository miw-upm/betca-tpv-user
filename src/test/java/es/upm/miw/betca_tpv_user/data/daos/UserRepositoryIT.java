package es.upm.miw.betca_tpv_user.data.daos;

import es.upm.miw.betca_tpv_user.TestConfig;
import es.upm.miw.betca_tpv_user.data.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static es.upm.miw.betca_tpv_user.data.model.Role.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByMobile() {
        assertTrue(this.userRepository.findByMobile("6").isPresent());
    }

    @Test
    void testFindByRoleIn() {
        List<Role> roles = List.of(ADMIN, MANAGER);
        assertTrue(this.userRepository.findByRoleIn(roles).stream().allMatch(user -> roles.contains(user.getRole())));
    }

    @Test
    void testFindByMobileAndFirstNameAndFamilyNameAndEmailAndDniNullSafeWithMobile() {
        assertTrue(this.userRepository.findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
                        "1", null, null, ".com", null, List.of(MANAGER)).stream()
                .anyMatch(user -> "666666001".equals(user.getMobile()))
        );
    }

    @Test
    void testFindByMobileAndFirstNameAndFamilyNameAndEmailAndDniNullSafeWithDni() {
        assertTrue(this.userRepository.findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
                null, null, null, null, "kk",
                List.of(ADMIN, MANAGER, OPERATOR, CUSTOMER)).isEmpty()
        );
    }

}
