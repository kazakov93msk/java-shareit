package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    List<Request> findAll();

    Request findById(Long itemRequestId);

    Request create(Request request);

    Request update(Request request);

    void deleteById(Long itemRequestId);
}
