package com.sbbc.rch.realchatapp.services;

import com.sbbc.rch.realchatapp.DTO.*;
import com.sbbc.rch.realchatapp.JWT.JWTService;
import com.sbbc.rch.realchatapp.model.User;
import com.sbbc.rch.realchatapp.repository.UserRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ResponseCookie getLogoutCookie() {
        return ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(true)
                .path("/") // важно не забыть
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    public UserDTO getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return Util.convertToUserDTO(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)));
    }

    public UserDTO signup(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already in use");
        }
        User user = User.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .email(registerRequestDTO.getEmail())
                .build();
        userRepository.save(user);
        return Util.convertToUserDTO(user);
    }


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + loginRequestDTO.getUsername() + " does not exist"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        String jwtToken = jwtService.generateToken(user.getId());

        return LoginResponseDTO.builder()
                .jwtToken(jwtToken)
                .userDTO(Util.convertToUserDTO(user))
                .build();
    }
}
