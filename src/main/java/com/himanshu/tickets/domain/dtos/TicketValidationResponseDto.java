package com.himanshu.tickets.domain.dtos;

import com.himanshu.tickets.domain.entities.TicketValidationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationResponseDto {

    private UUID ticketId;
    private TicketValidationStatusEnum status;
}
