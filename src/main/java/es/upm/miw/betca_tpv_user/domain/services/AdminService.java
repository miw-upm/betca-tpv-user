package es.upm.miw.betca_tpv_user.domain.services;

import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.data.model.User;
import es.upm.miw.betca_tpv_user.data.daos.AdminRepository;
import es.upm.miw.betca_tpv_user.domain.exceptions.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AdminService {

    private AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public void create(User user, Role role){
        if(Role.ADMIN.equals(role)){
            user.setRegistrationDate(LocalDateTime.now());
            this.adminRepository.save(user);
        }
    }

    public Stream<User> readAll() {
        return this.adminRepository.findAll().stream();
    }

    public User readByMobile(String mobile){
        return this.adminRepository.readByMobile(mobile).orElseThrow(() -> new NotFoundException("The mobile don't exist: " + mobile));
    }

    public Optional <User> update(String mobile, User user){
        return this.adminRepository.readByMobile(mobile)
                .map(dataUser -> {
                    BeanUtils.copyProperties(user, dataUser, "registrationDate");
                    return dataUser;
                }).flatMap(dataUser -> this.adminRepository.update(mobile, dataUser));
    }
}
