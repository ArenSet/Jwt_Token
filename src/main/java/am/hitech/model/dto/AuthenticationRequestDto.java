package am.hitech.model.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String email;

    private String password;
}
