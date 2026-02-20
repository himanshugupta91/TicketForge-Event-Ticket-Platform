package com.himanshu.tickets.services;

import com.himanshu.tickets.domain.entities.QrCode;
import com.himanshu.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);


}
