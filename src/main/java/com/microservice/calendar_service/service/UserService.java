package com.microservice.calendar_service.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.calendar_service.dto.UserRequestDto;
import com.microservice.calendar_service.model.User;
import com.microservice.calendar_service.repository.UserRepository;
import com.microservice.calendar_service.util.JwtUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> generateToken(UserRequestDto userDto) {
        try {
            if (userExists(userDto.getEmail())) {
                String token = tokenService.getTokenByEmail(userDto.getEmail());
                if (token == null) {
                    User user = getUserByEmail(userDto.getEmail());
                    
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("email", user.getEmail());
                    token = createToken(claims, user.getEmail());

                    tokenService.saveToken(user.getEmail(), token);
                    return ResponseEntity.ok(token);
                } else {
                    return ResponseEntity.ok(token);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("L'utilisateur n'existe pas");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating token");
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(jwtUtil.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public ResponseEntity<?> createUser(UserRequestDto userDto) {
        try {
            User findUser = userRepository.findByEmail(userDto.getEmail());
            if (findUser != null) {
                return ResponseEntity.badRequest().body("Cet utilisateur existe déjà");
            }

            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setFirstname(userDto.getFirstname());
            user.setLastname(userDto.getLastname());
            user.setCreatedTime(new Date());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'utilisateur");
        }
    }
}