package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final UserService userService;

    @GetMapping
    public List<RequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.validateExistence(userId);
        return RequestMapper.mapToRequestDto(requestService.findAllByUserId(userId));
    }

    @GetMapping("/{requestId}")
    public RequestDto findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId

    ) {
        userService.validateExistence(userId);
        return RequestMapper.mapToRequestDto(requestService.findById(requestId));
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Integer size
    ) {
        userService.validateExistence(userId);
        return RequestMapper.mapToRequestDto(requestService.findAll(userId, from, size));
    }

    @PostMapping
    public RequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid RequestDto requestDto
    ) {
        Request request = RequestMapper.mapToRequest(requestDto);
        request.setCreator(userService.findById(userId));
        return RequestMapper.mapToRequestDto(requestService.create(request));
    }
}
