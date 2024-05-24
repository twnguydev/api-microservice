package com.microservice.calendar_service.repository;

import com.microservice.calendar_service.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}