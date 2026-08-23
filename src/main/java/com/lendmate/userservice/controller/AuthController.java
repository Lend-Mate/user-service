package com.lendmate.userservice.controller;


import com.lendmate.userservice.dto.requestDto.AuthRequest;
import com.lendmate.userservice.entity.User;
import com.lendmate.userservice.service.JwtService;
import com.lendmate.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private UserService service;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    @GetMapping("/health")
    public String healthCheck() {
        return "user service is up...";
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody User user) {
        return service.createUser(user);
    }

    // Removed the role checks here as they are already managed in SecurityConfig
    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}