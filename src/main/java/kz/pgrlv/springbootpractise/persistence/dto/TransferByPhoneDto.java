package kz.pgrlv.springbootpractise.persistence.dto;

import lombok.Data;

@Data
public class TransferByPhoneDto {

    private String phoneNumber;
    private Double value;
}
