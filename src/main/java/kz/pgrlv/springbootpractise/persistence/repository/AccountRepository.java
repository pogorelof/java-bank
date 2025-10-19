package kz.pgrlv.springbootpractise.persistence.repository;

import kz.pgrlv.springbootpractise.persistence.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByCardNumber(String cardNumber);
    Optional<Account> getAccountByCardNumber(String cardNumber);
}
