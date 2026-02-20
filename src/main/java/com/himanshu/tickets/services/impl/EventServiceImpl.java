package com.himanshu.tickets.services.impl;

import com.himanshu.tickets.domain.CreateEventRequest;
import com.himanshu.tickets.domain.UpdateEventRequest;
import com.himanshu.tickets.domain.UpdateTicketTypeRequest;
import com.himanshu.tickets.domain.entities.Event;
import com.himanshu.tickets.domain.entities.EventStatusEnum;
import com.himanshu.tickets.domain.entities.TicketType;
import com.himanshu.tickets.domain.entities.User;
import com.himanshu.tickets.exceptions.EventUpdateException;
import com.himanshu.tickets.exceptions.TicketTypeNotFoundException;
import com.himanshu.tickets.repositories.EventRepository;
import com.himanshu.tickets.repositories.UserRepository;
import com.himanshu.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest event) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with id %s not found", organizerId))
                );

        Event eventToCreate = new Event();

        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(
                ticketType -> {
                    TicketType ticketTypeToCreate = new TicketType();
                    ticketTypeToCreate.setName(ticketType.getName());
                    ticketTypeToCreate.setPrice(ticketType.getPrice());
                    ticketTypeToCreate.setDescription(ticketType.getDescription());
                    ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    ticketTypeToCreate.setEvent(eventToCreate);
                    return ticketTypeToCreate;
                }).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStart(event.getStart());
        eventToCreate.setEnd(event.getEnd());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSalesStart(event.getSalesStart());
        eventToCreate.setSalesEnd(event.getSalesEnd());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setOrganizer(organizer);

        eventToCreate.setTicketTypes(ticketTypesToCreate);

       return eventRepository.save(eventToCreate);
    }

    @Override
    public Page<Event> listEventForOrganizer(UUID organizerId, Pageable pageable) {
       return eventRepository.findByOrganizerId(organizerId,pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Transactional
    @Override
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if (event.getId() == null) {
            throw new EventUpdateException("Event ID cannot be null");
        }

        if (!id.equals(event.getId())) {
            throw new EventUpdateException("Cannot update the ID of an event");
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(event.getId(), organizerId)
                .orElseThrow(() -> new EventUpdateException(
                        String.format("Event with id %s not found", event.getId())
                ));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(existingEventType ->
                !requestTicketTypeIds.contains(existingEventType.getId())
        );

        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for(UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if(null == ticketType.getId()) {
                // create case

                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketType.getName());
                ticketTypeToCreate.setPrice(ticketType.getPrice());
                ticketTypeToCreate.setDescription(ticketType.getDescription());
                ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);


            } else if (existingTicketTypesIndex.containsKey(ticketType.getId())) {
                // update case

                TicketType existingTicketType = existingTicketTypesIndex.get(ticketType.getId());

                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());



            } else {
                throw new TicketTypeNotFoundException(String.format("Ticket type with id %s not found", ticketType.getId()));
            }
        }

        return eventRepository.save(existingEvent);
    }

    @Transactional
    @Override
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
      getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvent(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }
}
