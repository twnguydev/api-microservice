package com.microservice.calendar_service.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String location;
    private Long userId;
    private String groupName;
    private Date createdTime = new Date();
    private Date updatedTime = null;
    private Date deletedTime = null;

    @OneToMany(mappedBy = "event")
    private List<Participant> participants;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;
}