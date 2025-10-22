package com.sbbc.rch.realchatapp.JWT;

import com.sbbc.rch.realchatapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.Collections;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private final JWTService jwtService;

    public JWTAuthFilter(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Long userId = null;
        String jwtToken = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }
        if (jwtToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JWT")) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
            if (jwtToken == null) {
                filterChain.doFilter(request, response);
            }

            userId = jwtService.extractUserId(jwtToken);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID:"));

                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
            return;
        }
    }
}
