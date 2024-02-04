package es.upm.miw.betca_tpv_user.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data //@ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tpvUser") // conflict with user table
public class User {
    @Id
    @GeneratedValue
    private int id;
    @NonNull
    @Column(unique = true, nullable = false)
    private String mobile;
    @NonNull
    private String firstName;
    private String familyName;
    private String email;
    private String dni;
    private String address;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime registrationDate;
    private Boolean active;
}
