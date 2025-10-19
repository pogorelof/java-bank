package kz.pgrlv.springbootpractise.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.pgrlv.springbootpractise.services.JwtService;
import kz.pgrlv.springbootpractise.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String username = null;
        UserDetails userDetails = null;
        UsernamePasswordAuthenticationToken auth = null;

        try {
            // Берем значение ключа авторизации из заголовка запроса
            String headerAuth = request.getHeader("Authorization");
            //Проверка на наличие ключа
            if (headerAuth != null && headerAuth.startsWith("Bearer ")){
                // После Bearer достаем ключ
                jwt = headerAuth.substring(7);
            }

            if (jwt != null){
                try {
                    // Достаем логин через нами созданный метод
                    username = jwtService.getNameFromJwt(jwt);
                } catch (ExpiredJwtException e){
                    // TODO
                }
                // Создать объект аутенфикации только в том случае, если его еще нет в контексте
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    // Загрузка userDetails
                    userDetails = userService.loadUserByUsername(username);
                    // Создание объекта аутенфикации
                    auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception e){

        }
        // Передаем данные запроса дальше по цепочке
        filterChain.doFilter(request, response);
    }
}
