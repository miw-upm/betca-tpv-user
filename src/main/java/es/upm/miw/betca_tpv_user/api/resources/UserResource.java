package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.EmailDto;
import es.upm.miw.betca_tpv_user.api.dtos.RecoverPasswordDto;
import es.upm.miw.betca_tpv_user.api.dtos.TokenDto;
import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_user.domain.services.JwtService;
import es.upm.miw.betca_tpv_user.domain.services.UserService;
import es.upm.miw.betca_tpv_user.domain.services.mail.MailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {
    public static final String USERS = "/users";
    public static final String CUSTOMERS = "/customers";
    public static final String PROFILE = "/profile";
    public static final String RECOVER = "/recover_password";
    public static final String SEND_EMAIL = "/send_email";
    public static final String TOKEN = "/token";
    public static final String MOBILE_ID = "/{mobile}";
    public static final String SEARCH = "/search";
    public final String MESSAGE = "Click on the next link, which allows you to update your password:\nhttp://localhost:4200/home/forgotten-password";

    private UserService userService;
    private MailService mailService;
    private JwtService jwtService;

    @Autowired
    public UserResource(UserService userService, MailService mailService, JwtService jwtService) {
        this.userService = userService;
        this.mailService = mailService;
        this.jwtService = jwtService;
    }

    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("authenticated")
    @PostMapping(value = TOKEN)
    public Optional< TokenDto > login(@AuthenticationPrincipal User activeUser) {
        return userService.login(activeUser.getUsername())
                .map(TokenDto::new);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void createUser(@Valid @RequestBody UserDto creationUserDto) {
        this.userService.createUser(creationUserDto.toUser(), this.extractRoleClaims());
    }

    @PreAuthorize("permitAll()")
    @PostMapping(value = CUSTOMERS)
    public void registerUser(@Valid @RequestBody UserDto creationUserDto) {
        creationUserDto.doDefault();
        if (creationUserDto.getRole().equals(Role.CUSTOMER)){
            this.userService.createUser(creationUserDto.toUser(), Role.CUSTOMER);
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping(SEND_EMAIL)
    public void sendEmail(@Valid @RequestBody EmailDto emailDto) {
        if (this.userService.readByEmail(emailDto.getEmail())!=null){
            this.mailService.send(emailDto.getEmail(), this.MESSAGE);
        }
    }

    @SecurityRequirement(name = "barerAuth")
    @PreAuthorize("authenticated")
    @PutMapping(MOBILE_ID)
    public void updateUser(@Valid @RequestBody UserDto updateUserDto, @PathVariable String mobile) {
        this.userService.updateUser(mobile, updateUserDto.toUser());
    }

    @PreAuthorize("permitAll()")
    @PutMapping(RECOVER)
    public void updateUserPassword(@Valid @RequestBody RecoverPasswordDto recoverPasswordDto) {
        this.userService.updateUserPassword(recoverPasswordDto.getMail(), new BCryptPasswordEncoder().encode(recoverPasswordDto.getPassword()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(MOBILE_ID)
    public UserDto readUser(@PathVariable String mobile) {
        return new UserDto(this.userService.readByMobile(mobile));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("authenticated")
    @GetMapping(PROFILE + MOBILE_ID)
    public UserDto readUserProfile(@PathVariable String mobile, @RequestHeader("Authorization") String token) {
        String extractedToken = this.jwtService.extractToken(token);
        if (!this.jwtService.user(extractedToken).equals(mobile)) {
            throw new ForbiddenException("You don't have access");
        }
        return new UserDto(this.userService.readByMobile(mobile));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Stream< UserDto > readAll() {
        return this.userService.readAll(this.extractRoleClaims())
                .map(UserDto::ofMobileFirstName);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(value = SEARCH)
    public Stream< UserDto > findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
            @RequestParam(required = false) String mobile, @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String familyName, @RequestParam(required = false) String email,
            @RequestParam(required = false) String dni) {
        return this.userService.findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
                mobile, firstName, familyName, email, dni, this.extractRoleClaims()
        ).map(UserDto::ofMobileFirstName);
    }

    private Role extractRoleClaims() {
        List< String > roleClaims = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        System.out.println(roleClaims);
        return Role.of(roleClaims.get(0));  // it must only be a role
    }

}
