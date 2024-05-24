package com.microservice.calendar_service.controller;

import com.microservice.calendar_service.dto.UserRequestDto;
import com.microservice.calendar_service.model.CustomUserDetails;
import com.microservice.calendar_service.util.JwtUtil;
import com.microservice.calendar_service.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userDto) {
        if (userDto.getEmail() == null || userDto.getFirstname() == null || userDto.getLastname() == null) {
            return ResponseEntity.badRequest().body("Les champs obligatoires ne sont pas renseignés");
        }
        if (StringUtils.isBlank(userDto.getEmail()) || StringUtils.isBlank(userDto.getFirstname()) || StringUtils.isBlank(userDto.getLastname())) {
            return ResponseEntity.badRequest().body("Les champs obligatoires ne sont pas renseignés");
        }
        if (!userDto.isValidEmail()) {
            return ResponseEntity.badRequest().body("L'email n'est pas au format attendu");
        }
        if (!userDto.isValidFirstname() || !userDto.isValidLastname()) {
            return ResponseEntity.badRequest().body("Le prénom ou le nom de famille n'est pas au format attendu");
        }

        ResponseEntity<?> user = userService.createUser(userDto);
        return user;
    }

    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestBody UserRequestDto userDto) {
        ResponseEntity<?> token = userService.generateToken(userDto);
        return token;
    }

    @GetMapping("/validate")
    public String validateToken(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            String email = payload.get("email");
            if (jwtUtil.validateToken(token, email)) return "Valid token";
            else return "Invalid token";
        } catch (Exception e) {
            return "Error validating token";
        }
    }

    @GetMapping("/extract")
    public String extractEmail(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            return jwtUtil.extractEmail(token);
        } catch (Exception e) {
            return "Error extracting email";
        }
    }

    @GetMapping("/me")
    public CustomUserDetails getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}