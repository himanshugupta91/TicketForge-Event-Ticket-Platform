package com.himanshu.tickets.services;

import com.himanshu.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface TicketTypeService {

    Ticket purchaseTicket(UUID userId, UUID ticketTypeId);

}
