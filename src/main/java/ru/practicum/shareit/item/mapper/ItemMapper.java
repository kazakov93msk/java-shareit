package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
@Component
public class ItemMapper {

    static public Item mapToItem(InputItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    static public OutputItemDto mapToItemDto(
            Item item,
            Long userId
    ) {
        OutputItemDto itemDto = OutputItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments() != null ?
                        CommentMapper.mapToCommentDto(item.getComments()) : new ArrayList<>())
                .build();

        if (userId.equals(item.getOwner().getId())) {
            itemDto.setLastBooking(item.getLastBooking() != null ?
                    BookingMapper.mapToShortBookingDto(item.getLastBooking()) : null);
            itemDto.setNextBooking(item.getNextBooking() != null ?
                    BookingMapper.mapToShortBookingDto(item.getNextBooking()) : null);
        }
        return itemDto;
    }

    static public ShortItemDto mapToShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    static public List<OutputItemDto> mapToItemDto(List<Item> items, Long userId) {
        return items.stream().map(item -> mapToItemDto(item, userId)).collect(Collectors.toList());
    }
}