package com.sbbc.rch.realchatapp.controllers;

import com.sbbc.rch.realchatapp.DTO.LoginRequestDTO;
import com.sbbc.rch.realchatapp.DTO.LoginResponseDTO;
import com.sbbc.rch.realchatapp.DTO.RegisterRequestDTO;
import com.sbbc.rch.realchatapp.DTO.UserDTO;
import com.sbbc.rch.realchatapp.repository.UserRepository;
import com.sbbc.rch.realchatapp.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(authService.signup(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        ResponseCookie responseCookie = ResponseCookie.from("JWT", loginResponseDTO.getJwtToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponseDTO.getUserDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie cookie = authService.getLogoutCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout successful");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication));
    }
}
