package kz.pgrlv.springbootpractise.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kz.pgrlv.springbootpractise.persistence.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    // Получение секретного ключа и жизни токена из application файла
    @Value("${token.signing.key}")
    private String secret;
    @Value("${token.expirationMs}")
    private int lifetime;

    // Генерация токена
    public String generateToken(Authentication authentication){
        // Данные пользователя
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //ToDo: Создать один раз

        // Генерация из secret шифрованного ключа подходящего под подписание
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Генерация и возвращения JWT токена
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                // Когда был создан токен
                .issuedAt(new Date())
                // Когда истекает: с текущего момента + время динзни токена
                .expiration(new Date(System.currentTimeMillis() + lifetime))
                // Подпись
                .signWith(key)
                // Сборка токена в строку
                .compact();
    }

    // Получить username из токена
    public String getNameFromJwt(String jwt) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload().getSubject();
    }
}
