package kz.pgrlv.springbootpractise.configuration;

import kz.pgrlv.springbootpractise.services.UserService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserService userService;
    private TokenFilter tokenFilter;

    public SecurityConfig(){}

    @Autowired
    public void setUserService(UserService userService) {this.userService = userService; }

    @Autowired
    public void setTokenFilter(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    // Определение типа кодирования пароля
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Определение главного объекта, который проверяет аутенфикацию
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Настройка менеджера аутенфикации
//    @Bean
//    @Primary
//    public AuthenticationManagerBuilder configureAuthenticationManagerBuilder(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder
//                // Указание сервиса, который реализует интерфейс UserDetailsService
//                .userDetailsService(userService)
//                // Указание как кодировать пароль
//                .passwordEncoder(passwordEncoder());
//        return authenticationManagerBuilder;
//    }

    // Настройка менеджера аутенфикации
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Указываем сервис UserDetails
        authProvider.setUserDetailsService(userService);
        // Указываем правила кодирования пароля
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Цепочка фильтров
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключение csrf
                .csrf(csrf -> csrf.disable())
                // Обход настройки cors, доступ для всех
                .cors(cors -> cors.configurationSource(
                        request -> new CorsConfiguration().applyPermitDefaultValues()
                )) // ToDo: This is Bad Practice
                // Обработка ошибки, когда пользователь не аутенфицирован
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll() // Доступ у всех()
                        .requestMatchers("/bankomat/**").permitAll()
                        .requestMatchers("/api/v1/user/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated() // Все остальные запросы только у аутенфицированных
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
