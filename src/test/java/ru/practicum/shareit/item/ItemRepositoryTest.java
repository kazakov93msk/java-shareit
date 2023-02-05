package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.util.ItemTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;
import static ru.practicum.shareit.utility.PageableBuilder.getItemDefaultPageable;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final EntityManager em;
    @Autowired
    private ItemRepository itemRep;

    @Test
    void callSearch() {
        User owner = getNewUser();
        Item item = getNewItem(owner);

        em.persist(owner);
        em.persist(item);

        List<Item> items = itemRep.search("escr", getItemDefaultPageable()).getContent();

        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }
}
