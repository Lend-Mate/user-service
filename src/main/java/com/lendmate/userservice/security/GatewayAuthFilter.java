package com.lendmate.userservice.security;

import com.lendmate.userservice.entity.Permission;
import com.lendmate.userservice.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Value("${gateway.security.enabled:true}")
    private boolean securityEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!securityEnabled) {
            // Geliştirme modunda tüm permission'ları ver
            List<SimpleGrantedAuthority> authorities = Arrays.stream(Permission.values())
                    .map(p -> new SimpleGrantedAuthority(p.name()))
                    .toList();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken("dev-user", null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
            return;
        }

        String role = request.getHeader("X-User-Role");
        String email = request.getHeader("X-User-Email");

        if (role != null && email != null) {
            try {
                Role userRole = Role.valueOf(role);
                List<SimpleGrantedAuthority> authorities = userRole.getPermissions().stream()
                        .map(p -> new SimpleGrantedAuthority(p.name()))
                        .toList();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (IllegalArgumentException e) {
                // geçersiz role, devam et
            }
        }

        filterChain.doFilter(request, response);
    }
}