package com.himanshu.tickets.controllers;

import com.himanshu.tickets.domain.dtos.GetEventDetailsResponseDto;
import com.himanshu.tickets.domain.dtos.ListPublishedEventResponseDto;
import com.himanshu.tickets.domain.entities.Event;
import com.himanshu.tickets.mappers.EventMappers;
import com.himanshu.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMappers eventMappers;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable){

        Page<Event> events;
        if(q != null && !q.trim().isEmpty()){
            events = eventService.searchPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(
                events.map(eventMappers::toListPublishedEventResponseDto));
    }


    @GetMapping(path = "/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getPublishedEventDetails(@PathVariable UUID eventId){
        return eventService.getPublishedEvent(eventId)
                .map(eventMappers::toGetEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
