package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper
@Slf4j
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
        log.debug(itemDto.toString());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        log.debug(itemDto.toString());
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
        log.debug(item.toString());
        return item;
    }
}
