package kz.pgrlv.springbootpractise.persistence.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupDto {

    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}
