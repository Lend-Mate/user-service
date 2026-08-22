package com.lendmate.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, GatewayAuthFilter gatewayAuthFilter, AuthenticationProvider authenticationProvider) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/auth/health",
                                "/auth/register",
                                "/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Kullanıcı kendi profilini yönetir
                        .requestMatchers(HttpMethod.GET, "/user/profile").hasAuthority("PROFILE_READ")
                        .requestMatchers(HttpMethod.PUT, "/user/profile").hasAuthority("PROFILE_WRITE")
                        .requestMatchers(HttpMethod.DELETE, "/user/profile").hasAuthority("PROFILE_DELETE")

                        // Kullanıcı kendi kiralamalarını görür
                        .requestMatchers(HttpMethod.GET, "/user/rentals").hasAuthority("RENTAL_READ")

                        // Sadece admin tüm kullanıcıları yönetir
                        .requestMatchers(HttpMethod.GET, "/user/admin/users").hasAuthority("USER_READ")
                        .requestMatchers(HttpMethod.GET, "/user/admin/users/{id}").hasAuthority("USER_READ")
                        .requestMatchers(HttpMethod.DELETE, "/user/admin/users/{id}").hasAuthority("USER_DELETE")
                        .requestMatchers(HttpMethod.PUT, "/user/admin/users/{id}/role").hasAuthority("ADMIN_PANEL")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(gatewayAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(gatewayAuthFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}