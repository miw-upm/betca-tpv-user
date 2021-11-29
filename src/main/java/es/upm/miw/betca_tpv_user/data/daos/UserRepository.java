package es.upm.miw.betca_tpv_user.data.daos;

import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository< User, Integer > {
    Optional< User > findByMobile(String mobile);

    List< User > findByRoleIn(Collection< Role > roles);

    @Query("select u from User u where " +
            "(?1 is null or u.mobile like concat('%',?1,'%')) and " +
            "(?2 is null or lower(u.firstName) like lower(concat('%',?2,'%'))) and" +
            "(?3 is null or lower(u.familyName) like lower(concat('%',?3,'%'))) and" +
            "(?4 is null or lower(u.email) like lower(concat('%',?4,'%'))) and" +
            "(?5 is null or lower(u.dni) like lower(concat('%',?5,'%'))) and" +
            "(u.role in ?6)")
    List< User > findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
            String mobile, String firstName, String familyName, String email, String dni, Collection< Role > roles);
}
