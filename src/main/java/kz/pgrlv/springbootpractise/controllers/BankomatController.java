package kz.pgrlv.springbootpractise.controllers;

import kz.pgrlv.springbootpractise.persistence.dto.CardDto;
import kz.pgrlv.springbootpractise.persistence.entity.Account;
import kz.pgrlv.springbootpractise.services.AccountService;
import kz.pgrlv.springbootpractise.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/bankomat")
@AllArgsConstructor
public class BankomatController {
    private final AccountService accountService;

    @PostMapping("/deposit/{sum}")
    ResponseEntity<?> deposit(@PathVariable Double sum, @RequestBody CardDto cardDto){
        boolean result = accountService.deposit(cardDto, sum);
        if (!result){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка в данных!");
        }
        return ResponseEntity.ok("Deposit has been replenished");
    }

    @PostMapping("/withdraw/{sum}")
    ResponseEntity<?> withdraw(@PathVariable Double sum, @RequestBody CardDto cardDto){
        boolean result = accountService.withdraw(cardDto, sum);
        if (!result){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка в данных!");
        }
        return ResponseEntity.ok("Success withdraw");
    }

    @PostMapping("/check")
    ResponseEntity<Boolean> check(@RequestBody CardDto cardDto){
        if(accountService.cardCheck(cardDto)){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/balance")
    ResponseEntity<Double> getBalance(@RequestBody CardDto cardDto){
        Double balance = accountService.getBalanceByCard(cardDto);
        if (balance == -1.0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
        return ResponseEntity.ok(balance);
    }
}
