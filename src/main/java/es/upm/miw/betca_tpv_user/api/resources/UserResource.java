package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.TokenDto;
import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@Log4j2
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {
    public static final String USERS = "/users";
    public static final String TOKEN = "/token";
    public static final String MOBILE_ID = "/{mobile}";
    public static final String SEARCH = "/search";

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("authenticated")
    @PostMapping(value = TOKEN)
    public TokenDto login(@AuthenticationPrincipal User activeUser) {
        TokenDto token = new TokenDto(userService.login(activeUser.getUsername()));
        log.debug(token::toString);
        return token;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void createUser(@Valid @RequestBody UserDto creationUserDto) {
        this.userService.createUser(creationUserDto.toUser(), this.extractRoleClaims());
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(MOBILE_ID)
    public UserDto readUser(@PathVariable String mobile) {
        return new UserDto(this.userService.findByMobileAssured(mobile));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Stream<UserDto> readAll() {
        return this.userService.readAll(this.extractRoleClaims())
                .map(UserDto::ofMobileFirstName);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(value = SEARCH)
    public Stream<UserDto> findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String familyName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String dni) {
        return this.userService.findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
                mobile, firstName, familyName, email, dni, this.extractRoleClaims()
        ).map(UserDto::ofMobileFirstName);
    }

    private Role extractRoleClaims() {
        List<String> roleClaims = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        return Role.of(roleClaims.getFirst());
    }

}
