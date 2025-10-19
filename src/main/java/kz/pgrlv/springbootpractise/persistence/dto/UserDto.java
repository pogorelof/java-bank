package kz.pgrlv.springbootpractise.persistence.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class UserDto {

    private String username;
    private LocalDate dateOfBirth;
    private String email;
    private Integer age;

    public UserDto(String username, LocalDate dateOfBirth, String email) {
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
