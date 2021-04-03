package es.upm.miw.betca_tpv_user.domain.services;

import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.daos.AdminRepository;
import es.upm.miw.betca_tpv_user.data.model.User;
import es.upm.miw.betca_tpv_user.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_user.domain.exceptions.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AdminService {

    private AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public void create(User user){
        user.setRegistrationDate(LocalDateTime.now());
        this.checkMobile(user.getMobile());
        this.adminRepository.save(user);
    }

    public Stream<User> readAll() {
        return this.adminRepository.findAll().stream();
    }


    public User readByMobile(String mobile) {
        return this.adminRepository.findByMobile(mobile).orElseThrow(() -> new NotFoundException("The mobile don't exist: " + mobile));

    }
    /*
    public User readByMobile(String mobile){
        return this.adminRepository.findByMobile(mobile).orElse(null);
    }*/


    public void update(String mobile, User user){
        User oldUser = this.adminRepository.findByMobile(mobile).orElseThrow(() -> new NotFoundException("The mobile don't exist: " + mobile));
        BeanUtils.copyProperties(user, oldUser, "id", "password", "registrationDate");
        this.adminRepository.save(oldUser);
    }

    public void delete(User user){
        this.adminRepository.delete(user);
    }

    private void checkMobile(String mobile) {
        if (this.adminRepository.findByMobile(mobile).isPresent()) {
            throw new ConflictException("The mobile already exists: " + mobile);
        }
    }

}
