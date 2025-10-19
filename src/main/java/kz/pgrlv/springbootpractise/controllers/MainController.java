package kz.pgrlv.springbootpractise.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class MainController {

    @GetMapping("/user")
    // Principal автоматически подставляется Spring Security
    // Хранит информацию об аутенфицированном пользователе
    public String userAccess(Principal principal){
        // Возвращение username пользователя
        return principal.getName();
    }
}
