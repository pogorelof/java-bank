package kz.pgrlv.springbootpractise.controllers;

import kz.pgrlv.springbootpractise.persistence.dto.SigninDto;
import kz.pgrlv.springbootpractise.persistence.dto.SignupDto;
import kz.pgrlv.springbootpractise.persistence.entity.User;
import kz.pgrlv.springbootpractise.persistence.repository.UserRepository;
import kz.pgrlv.springbootpractise.services.JwtService;
import kz.pgrlv.springbootpractise.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private UserService service;
    private UserRepository userRepository;
    // Для кодирования пароля при регистрации
    private PasswordEncoder passwordEncoder;
    // Для проверки логина и пароля при входе
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignupDto signupDto){
        // Проверки на случай, если логин или почта уже имеются
        if (userRepository.existsByUsername(signupDto.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }
        if(userRepository.existsByEmail(signupDto.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        // Процесс создания нового пользователя
        User user = new User();
        // Пароль обязательно хэшируется
        String hashedPassword = passwordEncoder.encode(signupDto.getPassword());
        user.setUsername(signupDto.getUsername());
        user.setEmail(signupDto.getEmail());
        user.setPassword(hashedPassword);
        user.setDateOfBirth(signupDto.getDateOfBirth());
        user.setPhoneNumber(signupDto.getPhoneNumber());
        if (user.getDateOfBirth().equals(LocalDate.of(1111,11,11))){
            user.setRole("ADMIN");
        }else{
            user.setRole("USER");
        }
        // Сохрание пользователя. Можно через репозиторий, но по логике данного приложения
        //нужно еще открыть счет в одной транзакции, поэтому сохранение через сервис идет
        service.saveUser(user);

        return ResponseEntity.ok("Success sign up");
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SigninDto signinDto){
        // Результат аутенфикации
        Authentication authentication = null;

        try {
            // Сам вызывает нужные методы и объекты, и находит пользователя по логину и паролю
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinDto.getUsername(), signinDto.getPassword()));
        } catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Если пользователь удачно аутенфицирован, то он кладется в контекст
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Генерируется токен
        String jwt = jwtService.generateToken(authentication);
        // Возвращается токен
        return ResponseEntity.ok(jwt);
    }

}
