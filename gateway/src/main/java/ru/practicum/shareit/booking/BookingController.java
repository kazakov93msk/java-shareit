package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.property.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utility.RequestUtil.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> findById(
            @PathVariable Long bookingId,
            @RequestHeader(HEADER_USER_ID) Long userId) {
        log.debug("GET: Get booking where owner or booker ID = {}.", userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "1") Long from,
            @Positive @RequestParam(defaultValue = "30") Integer size
    ) {
        BookingState bookingState = BookingState.checkState(state);
        log.debug("GET: Get bookings where booker ID = {}.", userId);
        return bookingClient.findAllByBooker(userId, bookingState, from, size);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getAllByItemsOwner(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "1") Long from,
            @Positive @RequestParam(defaultValue = "30") Integer size
    ) {
        BookingState bookingState = BookingState.checkState(state);
        log.debug("GET: Get bookings where owner ID = {}.", userId);
        return bookingClient.findAllByItemsOwner(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        log.debug("POST: Create booking {} with owner ID = {}.", bookingDto, userId);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.debug("PATCH: Approve booking {} where owner ID = {}. Decision: {}", bookingId, userId, approved);
        return bookingClient.approve(userId, bookingId, approved);
    }
}
