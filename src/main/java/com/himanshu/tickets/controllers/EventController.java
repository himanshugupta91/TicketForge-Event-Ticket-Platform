package com.himanshu.tickets.controllers;

import com.himanshu.tickets.domain.CreateEventRequest;
import com.himanshu.tickets.domain.UpdateEventRequest;
import com.himanshu.tickets.domain.dtos.*;
import com.himanshu.tickets.domain.entities.Event;
import com.himanshu.tickets.mappers.EventMappers;
import com.himanshu.tickets.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.himanshu.tickets.util.JwtUtil.parseUserId;

@RestController
@RequestMapping(path = "/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMappers eventMappers;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
           @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {

        CreateEventRequest createEventRequest = eventMappers.fromDto(createEventRequestDto);
        UUID userId = parseUserId(jwt);
        Event createdEvent = eventService.createEvent(userId, createEventRequest);

        CreateEventResponseDto createEventResponseDto = eventMappers.toDto(createdEvent);

        return new ResponseEntity<>(createEventResponseDto, HttpStatus.CREATED);

    }

    @PutMapping(path = "/{eventId}")
    public ResponseEntity<UpdateEventResponseDto> UpdateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {

        UpdateEventRequest updateEventRequest = eventMappers.fromDto(updateEventRequestDto);
        UUID userId = parseUserId(jwt);
        Event updatedEvent = eventService.updateEventForOrganizer(userId, eventId, updateEventRequest);

        UpdateEventResponseDto dUpdateEventResponseDto = eventMappers.toDUpdateEventResponseDto(updatedEvent);

        return ResponseEntity.ok(dUpdateEventResponseDto);

    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> listEvents(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable
    ){
        UUID userId = parseUserId(jwt);
        Page<Event> events = eventService.listEventForOrganizer(userId, pageable);
      return ResponseEntity.ok(
              events.map(eventMappers::toListEventResponseDto)
      );
    }


    @GetMapping(path = "/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId
    ){
        UUID userId = parseUserId(jwt);
        return eventService.getEventForOrganizer(userId, eventId)
                .map(eventMappers::toGetEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<Void> deleteEvent(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable UUID eventId){
        UUID userId = parseUserId(jwt);
        eventService.deleteEventForOrganizer(userId, eventId);
        return ResponseEntity.noContent().build();
    }







}
