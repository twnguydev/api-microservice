package com.microservice.calendar_service.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String email;
    private Date createdTime = new Date();
    private Date updatedTime = null;
    private Date deletedTime = null;

    @OneToMany(mappedBy = "userId")
    @JsonIgnore
    private List<Event> events;
}