package com.microservice.calendar_service.filter;

import com.microservice.calendar_service.util.JwtUtil;
import com.microservice.calendar_service.model.CustomUserDetails;
import com.microservice.calendar_service.model.User;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    
        final String authorizationHeader = request.getHeader("Authorization");
    
        String email = null;
        String jwt = null;
    
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                logger.error("Error extracting email from JWT", e);
            }
        }
    
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims;
            User user = new User();

            try {
                claims = jwtUtil.extractAllClaims(jwt);
                if (!jwtUtil.validateToken(jwt, claims.get("email", String.class))) {
                    logger.warn("Invalid or expired JWT token");
                } else {
                    user.setEmail(claims.get("email", String.class));
                    user.setFirstname(claims.get("firstname", String.class));
                    user.setLastname(claims.get("lastname", String.class));
                    user.setId(claims.get("id", Long.class));
                    user.setCreatedTime(new Date(Long.parseLong(claims.get("createdTime", String.class))));
                }
            } catch (Exception e) {
                logger.error("Error validating JWT token", e);
            }
    
            CustomUserDetails userDetails = new CustomUserDetails(user);
    
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    jwt,
                    userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}