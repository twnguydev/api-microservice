package com.microservice.calendar_service.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private String firstname;
    private String lastname;
    private String username = null;
    private String email;
    private Date createdTime = new Date();
    private Date updatedTime = null;
    private Date deletedTime = null;

    public CustomUserDetails(
        User user
    ) {
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.createdTime = user.getCreatedTime();
        this.updatedTime = user.getUpdatedTime();
        this.deletedTime = user.getDeletedTime();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] grantedEmails = {"tanguy.gibrat@epitech.eu", "romain.farinacci-berodias@epitech.eu"};
    
        for (String email : grantedEmails) {
            if (email.equals(this.email)) {
                return Collections.singletonList((GrantedAuthority) () -> "ROLE_USER");
            }
        }
        
        return Collections.emptyList();
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public Date getCreatedTime() {
        return this.createdTime;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }

    public Date getDeletedTime() {
        return this.deletedTime;
    }

    @Override
    public String getPassword() {
        return null;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}