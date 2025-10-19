package kz.pgrlv.springbootpractise.controllers;

import kz.pgrlv.springbootpractise.persistence.dto.ChangePhoneDto;
import kz.pgrlv.springbootpractise.persistence.dto.TransferByPhoneDto;
import kz.pgrlv.springbootpractise.persistence.dto.UserDto;
import kz.pgrlv.springbootpractise.persistence.entity.Account;
import kz.pgrlv.springbootpractise.persistence.entity.User;
import kz.pgrlv.springbootpractise.services.AccountService;
import kz.pgrlv.springbootpractise.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @GetMapping("/balance")
    ResponseEntity<Double> getBalance(Principal principal){
        Account account = userService.getUserByUsername(principal.getName()).getAccount();
        return ResponseEntity.ok(account.getBalance());
    }

    @GetMapping("/get_user/{phone}")
    ResponseEntity<UserDto> getUserByPhoneNumber(@PathVariable String phone){
        UserDto userDto = userService.getUserByPhoneNumber(phone);
        if (userDto == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/transfer/phone")
    ResponseEntity<Boolean> transferMoneyByPhone(@RequestBody TransferByPhoneDto transferData,  Principal principal){
        if (!accountService.transferMoneyByPhone(transferData, userService.getUserByUsername(principal.getName()))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/update/phone")
    ResponseEntity<String> changeNumber(Principal principal, @RequestBody ChangePhoneDto changePhoneDto){
        accountService.changeNumber(userService.getUserByUsername(principal.getName()), changePhoneDto.getNewNumber());
        return ResponseEntity.ok("Success");
    }
}
