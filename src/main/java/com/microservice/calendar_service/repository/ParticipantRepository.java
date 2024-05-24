package com.microservice.calendar_service.repository;

import com.microservice.calendar_service.model.Participant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> { 
}