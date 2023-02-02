package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.utility.PageableBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.util.ItemTestUtil.getItem;
import static ru.practicum.shareit.util.RequestTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.ANOTHER_USER_ID;
import static ru.practicum.shareit.util.UserTestUtil.USER_ID;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    RequestRepository requestRep;
    @Mock
    ItemRepository itemRep;
    RequestService requestService;
    final Pageable pageable = PageableBuilder.getRequestDefaultPageable();
    Item item;
    private static final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @BeforeEach
    void beforeEach() {
        requestService = new RequestServiceImpl(requestRep, itemRep);
        item = getItem();
        item.setRequest(getRequest(dt));
    }

    @Test
    void shouldReturnRequestsWhenCallFindAll() {
        when(requestRep.findAllByCreatorIdIsNot(USER_ID, pageable)).thenReturn(getRequestsPage(dt));
        when(requestRep.findAllByCreatorIdIsNot(ANOTHER_USER_ID, pageable)).thenReturn(new PageImpl<>(emptyList()));
        when(itemRep.findAllByRequestIn(getRequestsList(dt))).thenReturn(List.of(item));

        assertEquals(getRequestsPage(dt).getContent(), requestService.findAll(USER_ID, null, null));
        assertEquals(emptyList(), requestService.findAll(ANOTHER_USER_ID, null, null));
    }

    @Test
    void shouldReturnRequestsWhenCallFindAllByUserId() {
        when(requestRep.findByCreatorId(USER_ID, requestRep.CREATED_DESC)).thenReturn(getRequestsList(dt));
        when(requestRep.findByCreatorId(ANOTHER_USER_ID, requestRep.CREATED_DESC)).thenReturn(emptyList());
        when(itemRep.findAllByRequestIn(getRequestsList(dt))).thenReturn(List.of(item));

        assertEquals(getRequestsPage(dt).getContent(), requestService.findAllByUserId(USER_ID));

        assertEquals(emptyList(), requestService.findAllByUserId(ANOTHER_USER_ID));
    }

    @Test
    void shouldReturnRequestWhenCallFindById() {
        when(requestRep.findById(REQUEST_ID)).thenReturn(Optional.of(getRequest(dt)));
        when(requestRep.findById(ANOTHER_REQUEST_ID)).thenReturn(Optional.empty());

        assertEquals(getRequest(dt), requestService.findById(REQUEST_ID));
        assertThrows(NotFoundException.class, () -> requestService.findById(ANOTHER_REQUEST_ID));
    }

    @Test
    void shouldCreateRequestWhenCallCreate() {
        when(requestRep.existsById(REQUEST_ID)).thenReturn(false);
        when(requestRep.existsById(ANOTHER_REQUEST_ID)).thenReturn(true);
        when(requestRep.save(any())).thenAnswer(returnsFirstArg());

        assertEquals(getRequest(dt), requestService.create(getRequest(dt)));
        Request request = getRequest(dt);
        request.setId(ANOTHER_REQUEST_ID);
        assertThrows(AlreadyExistsException.class, () -> requestService.create(request));
    }
}
