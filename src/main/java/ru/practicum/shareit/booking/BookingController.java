package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("{bookingId}")
    public OutputBookingDto get(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET: Get booking where owner or booker ID = {}.", userId);
        return BookingMapper.mapToBookingDto(bookingService.findBookingById(userId, bookingId));
    }

    @GetMapping
    public List<OutputBookingDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.checkState(state);
        log.debug("GET: Get bookings where booker ID = {}.", userId);
        return BookingMapper.mapToBookingDto(bookingService.findAllByBookerId(userId, bookingState));
    }

    @GetMapping("owner")
    public List<OutputBookingDto> getAllByItemsOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.checkState(state);
        log.debug("GET: Get bookings where owner ID = {}.", userId);
        return BookingMapper.mapToBookingDto(bookingService.findAllByItemsOwnerId(userId, bookingState));
    }

    @PostMapping
    public OutputBookingDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody InputBookingDto bookingDto) {
        log.debug("POST: Create booking {} with owner ID = {}.", bookingDto, userId);
        User booker = userService.findUserById(userId);
        Item item = itemService.findItemById(bookingDto.getItemId());
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker);
        return BookingMapper.mapToBookingDto(bookingService.createBooking(userId, booking));
    }

    @PatchMapping("{bookingId}")
    public OutputBookingDto approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam @NotNull Boolean approved) {
        log.debug("PATCH: Approve booking {} where owner ID = {}. Decision: {}", bookingId, userId, approved);
        return BookingMapper.mapToBookingDto(bookingService.approveBookingById(userId, bookingId, approved));
    }
}
