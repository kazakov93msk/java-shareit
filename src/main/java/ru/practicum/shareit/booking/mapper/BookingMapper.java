package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.format.DateTimeFormatter;

@Mapper
@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public OutputBookingDto mapToBookingDto(Booking booking) {
        return OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().format(dateTimeFormatter))
                .end(booking.getEnd().format(dateTimeFormatter))
                .item(itemMapper.mapToShortItemDto(booking.getItem()))
                .booker(userMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Booking mapToBooking(InputBookingDto bookingDto, Long bookerId) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemService.findItemById(bookingDto.getItemId()))
                .booker(userService.findUserById(bookerId))
                .build();
    }
}