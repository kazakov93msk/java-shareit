package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    Booking findBookingByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime now
    );

    @Query("select new ru.practicum.shareit.booking.dto.ShortBookingDto(b.id, b.booker.id) " +
            "from Booking b " +
            "where b.item.id = ?1 and b.end < ?2 and b.booker.id <> b.item.owner.id " +
            "order by b.end desc")
    List<ShortBookingDto> findLastBooking(Long itemId, LocalDateTime now);

    @Query("select new ru.practicum.shareit.booking.dto.ShortBookingDto(b.id, b.booker.id) " +
            "from Booking b " +
            "where b.item.id = ?1 and b.start > ?2 and b.booker.id <> b.item.owner.id " +
            "order by b.start")
    List<ShortBookingDto> findNextBooking(Long itemId, LocalDateTime now);
}
