package kz.pgrlv.springbootpractise.persistence.entity;

import jakarta.persistence.*;
import lombok.*;


// Кредитная карта
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String cardNumber;
    private String expiration;
    private String cvv;

    public Account(User user){
        this.user = user;
    }
}
