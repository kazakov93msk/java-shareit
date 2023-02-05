package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemTestUtil {
    public static final String ITEM_DEFAULT_PATH = "/items";
    public static final Long ITEM_ID = 1L;
    public static final String ITEM_NAME = "firstItem";
    public static final String ITEM_DESCR = "firstItemDescr";
    public static final Long ANOTHER_ITEM_ID = 2L;
    public static final String ANOTHER_ITEM_DESCR = "anotherItemDescr";
    public static final String ITEM_PATH = ITEM_DEFAULT_PATH + "/" + ITEM_ID;

    public static Item getItem() {
        return Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCR)
                .available(true)
                .owner(UserTestUtil.getUser())
                .build();
    }

    public static Item getUpdatedItem() {
        return Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ANOTHER_ITEM_DESCR)
                .available(false)
                .owner(UserTestUtil.getUser())
                .build();
    }

    public static InputItemDto getInputItemDto() {
        return InputItemDto.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCR)
                .available(true)
                .build();
    }

    public static OutputItemDto getOutputDto(Long userId) {
        return ItemMapper.mapToItemDto(getItem(), userId);
    }

    public static Item getNewItem(User owner) {
        return Item.builder()
                .name(ITEM_NAME)
                .description(ITEM_DESCR)
                .available(true)
                .owner(owner)
                .build();
    }
}
