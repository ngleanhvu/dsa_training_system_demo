package com.ngleanhvu.dsa_training_system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

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
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/..").permitAll()
                        .requestMatchers("/ws/**").permitAll() // cho WS endpoint
                        .requestMatchers(HttpMethod.GET, "/api/v1/auths/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/login/admin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/google/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auths/github/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/discuss/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/discuss/search**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/discuss/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/programming-languages**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/contests/search**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stats/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/difficulties").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/topics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/problems/search**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/examples**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/examples/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tags").permitAll()
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:5174");
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Headers",
                "Access-Control-Allow-Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Origin",
                "Cache-Control",
                "Pragma",
                "X-Requested-With"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/ws/**", configuration);

        return source;
    }

}
