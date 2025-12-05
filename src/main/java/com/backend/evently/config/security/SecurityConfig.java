package com.backend.evently.config.security;

import com.backend.evently.config.filter.JwtAuthenticationFilter;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    private final String url = "/api/v1";

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthFilter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/exportacao-csv").hasRole("ADMIN")

                        .requestMatchers("/actuator/health-check").permitAll()

                        .requestMatchers(HttpMethod.GET, url + "/categorias/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, url + "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, url + "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, url + "/categorias/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, url + "/locais/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, url + "/locais/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, url + "/locais/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, url + "/locais/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, url + "/organizadores/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, url + "/organizadores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, url + "/organizadores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, url + "/organizadores/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, url + "/eventos/exportacao-csv").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, url + "/eventos/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, url + "/eventos/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, url + "/eventos/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, url + "/eventos/**").hasAnyRole("USUARIO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, url + "/eventos/**").hasAnyRole("USUARIO", "ADMIN")

                        .requestMatchers(url + "/ingressos/**").authenticated()

                        .requestMatchers(url + "/participacoes/**").authenticated()

                        .requestMatchers(HttpMethod.GET, url + "/favoritos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, url + "/favoritos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, url + "/favoritos/**").authenticated()

                        .requestMatchers(url + "/usuarios/me/**").hasAnyRole("USUARIO", "ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> (Usuario) usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}