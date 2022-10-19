package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRep;

    @Override
    public List<ItemRequest> findAllItemRequests() {
        return itemRequestRep.findAllItemRequests();
    }

    @Override
    public ItemRequest findItemRequestById(Long itemRequestId) {
        return itemRequestRep.findItemRequestById(itemRequestId).orElseThrow(
                () -> new NotFoundException(ItemRequest.class.toString(), itemRequestId)
        );
    }

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        if (itemRequest.getId() != null && itemRequestRep.itemRequestExists(itemRequest.getId())) {
            throw new AlreadyExistsException(ItemRequest.class.toString(), itemRequest.getId());
        }
        return itemRequestRep.createItemRequest(itemRequest);
    }

    @Override
    public ItemRequest updateItemRequest(ItemRequest itemRequest) {
        if (!itemRequestRep.itemRequestExists(itemRequest.getId())) {
            throw new NotFoundException(ItemRequest.class.toString(), itemRequest.getId());
        }
        return itemRequestRep.updateItemRequest(itemRequest);
    }

    @Override
    public void deleteItemRequestById(Long itemRequestId) {
        if (!itemRequestRep.itemRequestExists(itemRequestId)) {
            throw new NotFoundException(ItemRequest.class.toString(), itemRequestId);
        }
        itemRequestRep.deleteItemRequestById(itemRequestId);
    }
}
