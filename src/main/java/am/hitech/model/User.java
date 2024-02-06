package am.hitech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    private String number;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @Column(name = "number_verification")
    private Integer numberVerification;

    @JsonIgnore
    @Column(name = "email_verification")
    private Integer emailVerification;

    @JsonIgnore
    @Column(name = "forgot_password_code")
    private Integer forgotPasswordCode;

}
