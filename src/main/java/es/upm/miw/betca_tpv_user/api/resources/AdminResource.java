package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.domain.services.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping(AdminResource.ADMIN)
public class AdminResource {
    public static final String ADMIN = "/users-admin";

    private AdminService adminService;

    @Autowired
    public AdminResource(AdminService adminService){
        this.adminService = adminService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void create(@Valid @RequestBody UserDto creationUserDto) {
        this.adminService.create(creationUserDto.toUser(),this.extractRoleClaims());
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Stream< UserDto > readAll() {
        return this.adminService.readAll()
                .map(UserDto::ofMobileFirstName);
    }



    private Role extractRoleClaims() {
        List< String > roleClaims = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        System.out.println(roleClaims);
        return Role.of(roleClaims.get(0));  // it must only be a role
    }

}
