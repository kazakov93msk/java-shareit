package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRep;

    @Override
    public List<Request> findAllRequests() {
        return requestRep.findAll();
    }

    @Override
    public Request findRequestById(Long requestId) {
        Request request = requestRep.findById(requestId).orElseThrow(
                () -> new NotFoundException(Request.class.toString(), requestId)
        );
        log.debug("RequestService: Request {} returned.", request);
        return request;
    }

    @Override
    @Transactional
    public Request createRequest(Request request) {
        if (request.getId() != null && requestRep.existsById(request.getId())) {
            throw new AlreadyExistsException(Request.class.toString(), request.getId());
        }
        request = requestRep.save(request);
        log.debug("RequestService: Request {} created.", request);
        return request;
    }

    @Override
    @Transactional
    public Request updateRequest(Request request) {
        if (!requestRep.existsById(request.getId())) {
            throw new NotFoundException(Request.class.toString(), request.getId());
        }
        log.debug("RequestService: Feature not realized now.");
        return requestRep.save(request);
    }

    @Override
    @Transactional
    public void deleteRequestById(Long requestId) {
        if (!requestRep.existsById(requestId)) {
            throw new NotFoundException(Request.class.toString(), requestId);
        }
        requestRep.deleteById(requestId);
        log.debug("RequestService: Request with ID = {} deleted.", requestId);
    }
}
