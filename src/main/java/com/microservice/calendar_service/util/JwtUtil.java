package com.microservice.calendar_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import com.microservice.calendar_service.model.User;
import com.microservice.calendar_service.repository.UserRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserRepository userRepository;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public SecretKey getSecretKey() {
        return getSigningKey();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, String email) {
        String extractedEmail = extractEmail(token);
        User user = getUserByEmail(extractedEmail);
        return (
                extractedEmail.equals(email)
                && !isTokenExpired(token)
                && claimsMatchUser(token, user)
        );
    }

    private boolean claimsMatchUser(String token, User user) {
        Claims claims = extractAllClaims(token);
        return (
                claims.get("firstname", String.class).equals(user.getFirstname())
                && claims.get("lastname", String.class).equals(user.getLastname())
                && claims.get("id", Long.class).equals(user.getId())
        );
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}