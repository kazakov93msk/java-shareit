package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @GetMapping("{bookingId}")
    public OutputBookingDto get(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return bookingMapper.mapToBookingDto(bookingService.findBookingById(userId, bookingId));
    }

    @GetMapping
    public List<OutputBookingDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllByBookerId(userId, state).stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("owner")
    public List<OutputBookingDto> getAllByItemsOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String bookingState) {
        return bookingService.findAllByItemsOwnerId(userId, bookingState).stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public OutputBookingDto create(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @Valid @RequestBody InputBookingDto bookingDto) {
        Booking booking = bookingMapper.mapToBooking(bookingDto, userId);
        return bookingMapper.mapToBookingDto(bookingService.createBooking(userId, booking));
    }

    @PatchMapping("{bookingId}")
    public OutputBookingDto approve(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestParam @NotNull Boolean approved) {
        return bookingMapper.mapToBookingDto(bookingService.approveBookingById(userId, bookingId, approved));
    }


}
