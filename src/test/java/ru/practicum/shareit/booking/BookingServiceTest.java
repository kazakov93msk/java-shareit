package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingState;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.util.BookingTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRep;
    @Mock
    UserService userService;
    BookingService bookingService;

    private final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRep, userService);
    }

    @Test
    void shouldReturnBookingsWhenCallFindAllByBookerId() {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(bookingRep.findByUserId(any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserIdAndStatus(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserCurrent(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserFuture(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserPast(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));

        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.ALL, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.WAITING, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.REJECTED, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.CURRENT, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.FUTURE, null, null).get(0));
        assertEquals(getBookingsList(dt),
                bookingService.findAllByBookerId(USER_ID, BookingState.PAST, null, null));
    }

    @Test
    void shouldReturnBookingsWhenCallFilterUserBookingsByState() {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(bookingRep.findByUserId(any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserIdAndStatus(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserCurrent(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserFuture(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserPast(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));

        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.ALL, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.WAITING, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.REJECTED, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.CURRENT, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBookerId(USER_ID, BookingState.FUTURE, null, null).get(0));
        assertEquals(getBookingsList(dt),
                bookingService.findAllByBookerId(USER_ID, BookingState.PAST, null, null));
    }

    @Test
    void shouldReturnBookingsWhenCallFindAllByItemOwnerId() {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(bookingRep.findByUserId(any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserIdAndStatus(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserCurrent(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserFuture(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserPast(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));

        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, BookingState.ALL, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, BookingState.WAITING, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, BookingState.REJECTED, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, BookingState.CURRENT, null, null).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, BookingState.FUTURE, null, null).get(0));
        assertEquals(getBookingsList(dt),
                bookingService.findAllByItemsOwnerId(USER_ID, BookingState.PAST, null, null));
    }

    @Test
    void shouldReturnBookingOrThrowWhenCallFindById() {
        when(bookingRep.findById(BOOKING_ID)).thenReturn(Optional.of(getBooking(dt)));
        when(bookingRep.findById(ANOTHER_BOOKING_ID)).thenReturn(Optional.empty());

        assertEquals(getBooking(dt), bookingService.findById(USER_ID, BOOKING_ID));
        assertThrows(NotFoundException.class, () -> bookingService.findById(USER_ID, ANOTHER_BOOKING_ID));
        assertThrows(OperationAccessException.class, () -> bookingService.findById(ANOTHER_USER_ID, BOOKING_ID));
    }

    @Test
    void shouldReturnBookingOrThrowWhenCallApproveById() {
        when(bookingRep.findById(BOOKING_ID)).thenReturn(Optional.of(getBooking(dt)));
        when(bookingRep.findById(ANOTHER_BOOKING_ID))
                .thenReturn(Optional.of(getBookingWithStatus(dt, BookingStatus.APPROVED)));
        when(bookingRep.findById(3L))
                .thenReturn(Optional.empty());


        assertThrows(NotFoundException.class,
                () -> bookingService.approveById(USER_ID, 3L, true));
        assertThrows(OperationAccessException.class,
                () -> bookingService.approveById(ANOTHER_USER_ID, BOOKING_ID, true));
        assertThrows(NotAvailableException.class,
                () -> bookingService.approveById(USER_ID, ANOTHER_BOOKING_ID, true));

        assertEquals(getBookingWithStatus(dt, BookingStatus.APPROVED),
                bookingService.approveById(USER_ID, BOOKING_ID, true));
    }

    @Test
    void shouldCreateBookingWhenCallCreateBooking() {
        when(bookingRep.save(any())).thenAnswer(returnsFirstArg());

        assertThrows(OperationAccessException.class, () -> bookingService.create(USER_ID, getBooking(dt)));

        Booking booking = getBooking(dt);
        booking.getItem().setAvailable(false);
        assertThrows(NotAvailableException.class,
                () -> bookingService.create(ANOTHER_USER_ID, booking));

        assertEquals(getBooking(dt), bookingService.create(ANOTHER_USER_ID, getBooking(dt)));
    }
}
