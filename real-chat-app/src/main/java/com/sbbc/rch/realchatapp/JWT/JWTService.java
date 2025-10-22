package com.sbbc.rch.realchatapp.JWT;


import com.sbbc.rch.realchatapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public Long extractUserId(String jwtToken) {
        String id = extractClaims(jwtToken, claims -> claims.get("userId", String.class));
        return id != null ? Long.parseLong(id) : null;
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser().
                verifyWith(getSignInKey()).
                build().
                parseSignedClaims(jwtToken).
                getPayload();

    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 🔹 Генерация токена с userId
    public String generateToken(Long userId) {
        return Jwts.builder()
                .id(userId.toString()) // jti (claim "id")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    // 🔹 Валидация токена по userId
    public boolean isTokenValid(String jwtToken, User user) {

            final Long userIdFromToken = extractUserId(jwtToken);
            final Long userId = user.getId();

            return (userIdFromToken != null && userIdFromToken.equals(userId) && !isTokenExpired(jwtToken));

    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken, Claims::getExpiration);
    }
}
