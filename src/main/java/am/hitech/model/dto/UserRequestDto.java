package am.hitech.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRequestDto {

    @NotBlank(message = "The firstname can't be null")
    private String name;
    @NotBlank(message = "The lastname can't be null")
    private String lastName;
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")
    private String email;
}
