package kz.pgrlv.springbootpractise.persistence.dto;

import lombok.Data;

@Data
public class CardDto {
    private String cardNumber;
    private String expiration;
    private String cvv;
}
