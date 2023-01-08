package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    List<Request> findAllRequests();

    Request findRequestById(Long itemRequestId);

    Request createRequest(Request request);

    Request updateRequest(Request request);

    void deleteRequestById(Long itemRequestId);
}
