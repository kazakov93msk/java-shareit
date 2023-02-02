package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    List<Request> findAll(Long userId, Long start, Integer size);

    List<Request> findAllByUserId(Long userId);

    Request findById(Long itemRequestId);

    Request create(Request request);
}
