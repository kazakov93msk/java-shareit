package ru.practicum.shareit.utility;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.IllegalPageArgumentException;
import ru.practicum.shareit.item.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PageableBuilderTest {
    @Test
    void getPageable() {
        assertThrows(IllegalPageArgumentException.class, () ->
                PageableBuilder.getPageable(-1L, 30, ItemRepository.ID_ASC));
        assertThrows(IllegalPageArgumentException.class, () ->
                PageableBuilder.getPageable(1L, -30, ItemRepository.ID_ASC));
        assertThrows(IllegalPageArgumentException.class, () ->
                PageableBuilder.getPageable(1L, 30, null));
    }
}