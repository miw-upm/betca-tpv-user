package es.upm.miw.betca_tpv_user.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecoverPasswordDto {
    private String mail;
    private String password;
}
