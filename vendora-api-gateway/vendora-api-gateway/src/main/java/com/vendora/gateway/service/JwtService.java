package com.vendora.gateway.service;
 
import java.util.Base64;
 
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
 
    public JwtService(
            @Value("${jwt.secret}") String secret) {
 
        byte[] keyBytes = Decoders.BASE64.decode(secret);
 
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
 
    public Claims validateToken(String token) {
 
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
 
    public Long extractUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }
 
    public String extractEmail(Claims claims) {
        return claims.get("email", String.class);
    }
 
    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }
}
 