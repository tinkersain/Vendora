 
package com.vendora.auth.service;
 
import java.util.Date;
 
import javax.crypto.SecretKey;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
 
@Service
public class JwtService {
 
    private final SecretKey secretKey;
    private final long jwtExpiration;
 
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpiration) {
 
        // Decode the Base64 secret and generate a signing key
        byte[] keyBytes = Decoders.BASE64.decode(secret);
 
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }
 
    // Generate JWT token
    public String generateToken(Long userId, String email, String role) {
 
        Date currentDate = new Date();
 
        Date expirationDate =
                new Date(currentDate.getTime() + jwtExpiration);
 
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }
 
    // Extract all claims from a token after verifying its signature
    public Claims extractAllClaims(String token) {
 
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
 
    // Extract user ID
    public Long extractUserId(String token) {
 
        return Long.parseLong(
                extractAllClaims(token).getSubject()
        );
    }
 
    // Extract email
    public String extractEmail(String token) {
 
        return extractAllClaims(token).get("email", String.class);
    }
 
    // Extract role
    public String extractRole(String token) {
 
        return extractAllClaims(token).get("role", String.class);
    }
 
    // Return token expiration duration in milliseconds
    public long getExpirationInMillis() {
        return jwtExpiration;
    }
 
}
 