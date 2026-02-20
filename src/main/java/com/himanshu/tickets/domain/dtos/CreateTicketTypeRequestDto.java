package com.himanshu.tickets.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketTypeRequestDto {


    @NotBlank(message = "Ticket type name Is required")
    private String name;

    @NotNull(message = "Price is Required")
    @PositiveOrZero(message = "Price Must be zero or greater")
    private double price;


    private String description;


    private Integer totalAvailable;
}
