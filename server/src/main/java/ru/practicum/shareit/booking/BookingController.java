package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

import java.util.List;

import static ru.practicum.shareit.utility.RequestUtil.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("{bookingId}")
    public OutputBookingDto findById(
            @PathVariable Long bookingId,
            @RequestHeader(HEADER_USER_ID) Long userId) {
        log.debug("GET: Get booking where owner or booker ID = {}.", userId);
        return BookingMapper.mapToBookingDto(bookingService.findById(userId, bookingId));
    }

    @GetMapping
    public List<OutputBookingDto> findAllByBooker(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "1") Long from,
            @RequestParam(defaultValue = "30") Integer size
    ) {
        BookingState bookingState = BookingState.checkState(state);
        log.debug("GET: Get bookings where booker ID = {}.", userId);
        return BookingMapper.mapToBookingDto(bookingService.findAllByBookerId(userId, bookingState, from, size));
    }

    @GetMapping("owner")
    public List<OutputBookingDto> getAllByItemsOwner(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "1") Long from,
            @RequestParam(defaultValue = "30") Integer size
    ) {
        BookingState bookingState = BookingState.checkState(state);
        log.debug("GET: Get bookings where owner ID = {}.", userId);
        return BookingMapper.mapToBookingDto(bookingService.findAllByItemsOwnerId(userId, bookingState, from, size));
    }

    @PostMapping
    public OutputBookingDto create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestBody InputBookingDto bookingDto) {
        log.debug("POST: Create booking {} with owner ID = {}.", bookingDto, userId);
        User booker = userService.findById(userId);
        Item item = itemService.findById(bookingDto.getItemId());
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker);
        return BookingMapper.mapToBookingDto(bookingService.create(userId, booking));
    }

    @PatchMapping("{bookingId}")
    public OutputBookingDto approve(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.debug("PATCH: Approve booking {} where owner ID = {}. Decision: {}", bookingId, userId, approved);
        return BookingMapper.mapToBookingDto(bookingService.approveById(userId, bookingId, approved));
    }
}
