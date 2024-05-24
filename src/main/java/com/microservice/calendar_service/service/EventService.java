package com.microservice.calendar_service.service;

import com.microservice.calendar_service.dto.EventRequestDto;
import com.microservice.calendar_service.model.Event;
import com.microservice.calendar_service.model.Participant;
import com.microservice.calendar_service.repository.EventRepository;
import com.microservice.calendar_service.repository.UserRepository;
import com.microservice.calendar_service.model.User;
import com.microservice.calendar_service.repository.ParticipantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Transactional
    public ResponseEntity<?> createEvent(EventRequestDto eventRequestDto) {
        List<Long> participantIds = eventRequestDto.getParticipantIds();
        List<Long> invalidIds = new ArrayList<>();
        List<User> validUsers = new ArrayList<>();

        for (Long participantId : participantIds) {
            Optional<User> userOptional = userRepository.findById(participantId);
            if (userOptional.isPresent()) {
                validUsers.add(userOptional.get());
            } else {
                invalidIds.add(participantId);
            }
        }

        if (!invalidIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Les utilisateurs avec les IDs suivants n'existent pas : " + invalidIds);
        }
    
        try {
            Date startTime = parseDateString(eventRequestDto.getStartTime());
            Date endTime = parseDateString(eventRequestDto.getEndTime());
            if (startTime.after(endTime)) {
                return ResponseEntity.badRequest().body("L'heure de début doit être avant l'heure de fin");
            }

            Event event = new Event();
            event.setTitle(eventRequestDto.getTitle());
            event.setDescription(eventRequestDto.getDescription());
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setLocation(eventRequestDto.getLocation());
            event.setUserId(eventRequestDto.getUserId());
            event.setGroupName(eventRequestDto.getGroupName());

            User organizer = userRepository.findById(eventRequestDto.getUserId()).orElseThrow();

            event.setUser(organizer);
            Event savedEvent = eventRepository.save(event);

            List<Participant> participants = new ArrayList<>();
            for (User validUser : validUsers) {
                if (validUser.equals(organizer)) {
                    return ResponseEntity.badRequest().body("L'utilisateur organisateur ne peut pas être un participant");
                }
                Participant participant = new Participant();
                participant.setUser(validUser);
                participant.setEvent(savedEvent);
                participants.add(participantRepository.save(participant));
            }

            savedEvent.setParticipants(participants);
            return ResponseEntity.ok(savedEvent);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Les dates doivent être au format 'yyyy-MM-dd'T'HH:mm:ss'");
        }
    }    

    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartTime(eventDetails.getStartTime());
        event.setEndTime(eventDetails.getEndTime());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private Date parseDateString(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.parse(dateString);
    }
}