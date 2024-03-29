package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OutputBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ShortItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
