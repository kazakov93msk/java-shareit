package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
@UtilityClass
public class BookingMapper {

    public static OutputBookingDto mapToBookingDto(Booking booking) {
        return OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToShortItemDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBooking(InputBookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
    }

    public static ShortBookingDto mapToShortBookingDto(Booking booking) {
        return ShortBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static List<OutputBookingDto> mapToBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }
}