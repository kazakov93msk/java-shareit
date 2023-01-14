package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime now
    );

    Booking findTopByItem_IdAndBooker_IdIsNotAndEndBeforeOrderByEndDesc(Long itemId, Long ownerId, LocalDateTime now);

    Booking findTopByItem_IdAndBooker_IdIsNotAndStartAfterOrderByStart(Long itemId, Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "order by b.start desc ")
    List<Booking> findByUserId(Long userId, Boolean isOwner);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.status = ?3 " +
            "order by b.start desc ")
    List<Booking> findByUserIdAndStatus(Long userId, Boolean isOwner, BookingStatus status);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.start <= ?3 " +
            "  and b.end >= ?3 " +
            "order by b.start desc ")
    List<Booking> findByUserCurrent(Long userId, Boolean isOwner, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.start >= ?3 " +
            "order by b.start desc ")
    List<Booking> findByUserFuture(Long userId, Boolean isOwner, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.end <= ?3 " +
            "order by b.start desc ")
    List<Booking> findByUserPast(Long userId, Boolean isOwner, LocalDateTime now);
}
