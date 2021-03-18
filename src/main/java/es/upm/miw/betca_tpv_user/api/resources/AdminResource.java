package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.domain.services.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(AdminResource.ADMIN)
public class AdminResource {
    public static final String ADMIN = "/users-admin";
    public static final String MOBILE_ID = "/{mobile}";


    private AdminService adminService;

    @Autowired
    public AdminResource(AdminService adminService){
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void create(@Valid @RequestBody UserDto creationUserDto) {
        this.adminService.create(creationUserDto.toUser());
    }

    /*
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Stream< UserDto > readBasicInfo() {
        return this.adminService.readAll()
                .map(UserDto::ofMobileFirstNameRole);
    }*/


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Stream< UserDto > readAll() {
        return this.adminService.readAll()
                .map(UserDto::ofUser);
    }

    @SecurityRequirement(name = "barerAuth")
    @PreAuthorize("authenticated")
    @PutMapping(MOBILE_ID)
    public void update(@Valid @RequestBody UserDto updateUserDto, @PathVariable String mobile) {
        this.adminService.update(mobile, updateUserDto.toUser());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("authenticated")
    @GetMapping(MOBILE_ID)
    public UserDto readUser(@PathVariable String mobile) {
        return new UserDto(this.adminService.readByMobile(mobile));
    }


    private Role extractRoleClaims() {
        List< String > roleClaims = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        System.out.println(roleClaims);
        return Role.of(roleClaims.get(0));  // it must only be a role
    }

}
