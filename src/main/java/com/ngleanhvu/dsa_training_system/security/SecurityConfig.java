package com.ngleanhvu.dsa_training_system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/login/admin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/google/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/github/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/discuss/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/discuss/search**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/contests/search**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/difficulties").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/topics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/problems/search**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/examples**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/examples/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            log.debug("Role: {}", role);
            if (role == null) return List.of();
            String authority = "ROLE_" + role.toUpperCase();
            return List.of(new SimpleGrantedAuthority(authority));
        });

        return jwtConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        RSAPublicKey publicKey = RsaKeyUtil.getPublicKey();
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();

        decoder.setJwtValidator(JwtValidators.createDefault());

        return decoder;
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // các phương thức được phép
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}
