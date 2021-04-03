package es.upm.miw.betca_tpv_user.api.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoverPasswordDto {
    private String mail;
    private String password;
}
