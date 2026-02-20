package com.himanshu.tickets.services.impl;

import com.himanshu.tickets.domain.entities.Ticket;
import com.himanshu.tickets.domain.entities.TicketStatusEnum;
import com.himanshu.tickets.domain.entities.TicketType;
import com.himanshu.tickets.domain.entities.User;
import com.himanshu.tickets.exceptions.TicketTypeNotFoundException;
import com.himanshu.tickets.exceptions.TicketsSoldOutException;
import com.himanshu.tickets.exceptions.UserNotFoundException;
import com.himanshu.tickets.repositories.TicketRepository;
import com.himanshu.tickets.repositories.TicketTypeRepository;
import com.himanshu.tickets.repositories.UserRepository;
import com.himanshu.tickets.services.QrCodeService;
import com.himanshu.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id %s not found", userId)
        ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() -> new TicketTypeNotFoundException(
                String.format("Ticket type with id %s not found", ticketTypeId)
        ));

        int purchasedTicket = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTicket + 1 > totalAvailable) {
            throw new TicketsSoldOutException(
                    String.format("Ticket type with id %s has been sold out", ticketTypeId));
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);
        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);


    }
}
