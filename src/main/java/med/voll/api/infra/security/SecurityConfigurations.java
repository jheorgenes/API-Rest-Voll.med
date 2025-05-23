package med.voll.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Habilita personalização de segurança do Spring
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Mudando de STATEFUL para STATELESS
                .authorizeHttpRequests(auth -> {//Definindo as regras de autorização
                    auth.requestMatchers(HttpMethod.POST, "/login").permitAll(); //Permitindo a rota login
                    auth.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "swagger-ui/**").permitAll(); //Permitindo as rotas do swagger
                    auth.anyRequest().authenticated(); // Bloqueando as demais rotas
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) //IMPORTANTE: Definindo a ordem de chamada do Filter para securityFilter
                .build();
    }

    /**
     * Método auxiliador para injetar o AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Método auxiliador para injetar o PasswordEncoder usando BCryptPasswordEncoder como hash
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
