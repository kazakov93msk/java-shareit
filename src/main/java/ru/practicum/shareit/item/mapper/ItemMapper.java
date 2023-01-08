package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Mapper
@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingRepository bookingRep;
    private final CommentRepository commentRep;

    public Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto mapToItemDto(Item item, Long userId) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentRep.findDtoCommentsByItemId(item.getId()))
                .build();

        if (userId.equals(item.getOwner().getId())) {
            itemDto.setLastBooking(bookingRep.findLastBooking(item.getId(), LocalDateTime.now()).stream()
                    .findFirst().orElse(null));
            itemDto.setNextBooking(bookingRep.findNextBooking(item.getId(), LocalDateTime.now()).stream()
                            .findFirst().orElse(null));
        }
        return itemDto;
    }

    public ShortItemDto mapToShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}