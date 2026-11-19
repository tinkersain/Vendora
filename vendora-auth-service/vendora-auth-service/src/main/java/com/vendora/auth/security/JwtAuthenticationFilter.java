 
package com.vendora.auth.security;
 
import java.io.IOException;
 
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
 
import com.vendora.auth.service.JwtService;
 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
import java.util.List;
 
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 
    private final JwtService jwtService;
 
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
 
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
 
        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);
 
        // Continue if no Bearer token is provided
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {
 
            filterChain.doFilter(request, response);
            return;
        }
 
        String token = authorizationHeader.substring(7);
 
        try {
            // Verify token signature and expiration
            Claims claims = jwtService.extractAllClaims(token);
 
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
 
            if (userId == null || role == null || role.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid token claims");
                return;
            }
 
            // Create Spring Security authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
 
            // Store authenticated user in SecurityContext
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
 
        } catch (JwtException | IllegalArgumentException ex) {
 
            SecurityContextHolder.clearContext();
 
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token");
            return;
        }
 
        filterChain.doFilter(request, response);
    }
}
 