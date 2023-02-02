package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ToString.Exclude
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    @ToString.Exclude
    private User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id)
                && Objects.equals(start.truncatedTo(ChronoUnit.MINUTES), booking.start.truncatedTo(ChronoUnit.MINUTES))
                && Objects.equals(end.truncatedTo(ChronoUnit.MINUTES), booking.end.truncatedTo(ChronoUnit.MINUTES))
                && Objects.equals(item.getId(), booking.item.getId())
                && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, item, status);
    }
}
