package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.util.BookingTestUtil.getBooking;
import static ru.practicum.shareit.util.BookingTestUtil.getBookingsList;
import static ru.practicum.shareit.util.CommentTestUtil.getComment;
import static ru.practicum.shareit.util.ItemTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;
import static ru.practicum.shareit.utility.PageableBuilder.getItemDefaultPageable;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRep;
    @Mock
    CommentRepository commentRep;
    @Mock
    BookingRepository bookingRep;
    @Mock
    UserService userService;

    ItemService itemService;

    private final User user = getUser();
    private final Item item = getItem();
    private final Item updatedItem = getUpdatedItem();
    private final Pageable pageable = getItemDefaultPageable();
    private final Comment comment = getComment();

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRep, commentRep, bookingRep, userService);
    }

    @Test
    void shouldReturnItemListWhenCallFindByUserId() {
        when(itemRep.findAllByOwnerId(USER_ID, pageable)).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRep.findByItemInAndStatus(any(), any(), any()))
                .thenReturn(getBookingsList(LocalDateTime.now()));

        List<Item> items = itemService.findByUserId(USER_ID, null, null);

        verify(itemRep, times(1)).findAllByOwnerId(USER_ID, pageable);
        assertThat(items, notNullValue());
        assertEquals(1, items.size());
        assertEquals(ITEM_ID, items.get(0).getId());
        assertEquals(item.getOwner(), items.get(0).getOwner());
    }

    @Test
    void shouldReturnItemOrThrowWhenCallFindByItemId() {
        when(itemRep.findById(ITEM_ID)).thenReturn(Optional.ofNullable(item));
        when(itemRep.findById(ANOTHER_ITEM_ID)).thenReturn(Optional.empty());

        assertEquals(item, itemService.findById(ITEM_ID));
        verify(itemRep, times(1)).findById(ITEM_ID);
        verifyNoMoreInteractions(itemRep);

        assertThrows(NotFoundException.class, () -> itemService.findById(ANOTHER_ITEM_ID));
    }

    @Test
    void shouldReturnItemsListWhenCallSearchByText() {
        when(itemRep.search("descr", pageable)).thenReturn(new PageImpl<>(List.of(item)));
        when(itemRep.search("undefined", pageable)).thenReturn(new PageImpl<>(emptyList()));

        List<Item> items = itemService.searchByText("descr", null, null);
        assertThat(items, notNullValue());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getOwner(), items.get(0).getOwner());

        List<Item> empty = itemService.searchByText("undefined", null, null);
        assertThat(empty, notNullValue());
        assertEquals(0, empty.size());
    }

    @Test
    void shouldCreateAndReturnItemWhenCallCreate() {
        when(itemRep.save(any())).thenAnswer(returnsFirstArg());
        when(userService.findById(USER_ID)).thenReturn(user);

        Item returned = itemService.create(USER_ID, item);
        verify(userService, times(1)).findById(USER_ID);
        verify(itemRep, times(1)).save(any());
        assertEquals(item, returned);
    }

    @Test
    void shouldUpdateAndReturnItemWhenCallUpdate() {
        when(itemRep.findById(ITEM_ID)).thenReturn(Optional.ofNullable(item));
        when(itemRep.findById(ANOTHER_ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(USER_ID, ANOTHER_ITEM_ID, updatedItem));
        assertThrows(OperationAccessException.class, () -> itemService.update(ANOTHER_USER_ID, ITEM_ID, updatedItem));

        Item returned = itemService.update(USER_ID, ITEM_ID, updatedItem);
        assertEquals(updatedItem, returned);
        verify(itemRep, times(3)).findById(anyLong());
    }

    @Test
    void shouldDeleteItemWhenCallDeleteById() {
        when(itemRep.findById(ITEM_ID)).thenReturn(Optional.ofNullable(item));
        when(itemRep.findById(ANOTHER_ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.deleteById(USER_ID, ANOTHER_ITEM_ID));
        assertThrows(OperationAccessException.class, () -> itemService.deleteById(ANOTHER_USER_ID, USER_ID));

        itemService.deleteById(USER_ID, ITEM_ID);

        verify(itemRep, times(3)).findById(anyLong());
        verify(itemRep, times(1)).deleteById(ITEM_ID);
    }

    @Test
    void shouldCreateCommentWhenCallCreateComment() {
        when(bookingRep.findBookingByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(getBooking(LocalDateTime.now()));
        when(commentRep.save(any())).thenReturn(comment);

        Comment returned = itemService.createComment(ITEM_ID, USER_ID, comment);

        assertEquals(comment, returned);
        verify(bookingRep, times(1))
                .findBookingByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any());
        verify(commentRep, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCallCreateComment() {
        when(bookingRep.findBookingByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(null);
        assertThrows(NotAvailableException.class, () -> itemService.createComment(ITEM_ID, USER_ID, comment));
    }
}
