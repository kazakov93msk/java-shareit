package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.utility.PageableBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRep;
    private final ItemRepository itemRep;

    @Override
    public List<Request> findAll(Long userId, Long start, Integer size) {
        Pageable pageable = PageableBuilder.getPageable(start, size, requestRep.CREATED_DESC);
        List<Request> requests = requestRep.findAllByCreatorIdIsNot(userId, pageable).getContent();
        return setItems(requests);
    }

    @Override
    public List<Request> findAllByUserId(Long userId) {
        List<Request> requests = requestRep.findByCreatorId(userId, requestRep.CREATED_DESC);
        return setItems(requests);
    }

    @Override
    public Request findById(Long requestId) {
        Request request = requestRep.findById(requestId).orElseThrow(
                () -> new NotFoundException(Request.class.getSimpleName(), requestId)
        );
        log.debug("RequestService: Request {} returned.", request);
        request.setItems(itemRep.findAllByRequestId(requestId));
        return request;
    }

    @Override
    @Transactional
    public Request create(Request request) {
        if (request.getId() != null && requestRep.existsById(request.getId())) {
            throw new AlreadyExistsException(Request.class.getSimpleName(), request.getId());
        }
        request = requestRep.save(request);
        log.debug("RequestService: Request {} created.", request);
        request.setItems(itemRep.findAllByRequestId(request.getId()));
        return request;
    }

    private List<Request> setItems(List<Request> requests) {
        Map<Request, List<Item>> items = itemRep.findAllByRequestIn(requests).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Item::getRequest, Collectors.toList()));
        for (Request request : requests) {
            request.setItems(items.getOrDefault(request, List.of()));
        }
        return requests;
    }
}
