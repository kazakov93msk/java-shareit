package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.utility.RequestUtil.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final UserService userService;

    @GetMapping
    public List<RequestDto> findAllByUserId(@RequestHeader(HEADER_USER_ID) Long userId) {
        userService.validateExistence(userId);
        return RequestMapper.mapToRequestDto(requestService.findAllByUserId(userId));
    }

    @GetMapping("/{requestId}")
    public RequestDto findById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long requestId
    ) {
        userService.validateExistence(userId);
        return RequestMapper.mapToRequestDto(requestService.findById(requestId));
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "1") Long from,
            @RequestParam(defaultValue = "30") Integer size
    ) {
        userService.validateExistence(userId);
        return RequestMapper.mapToRequestDto(requestService.findAll(userId, from, size));
    }

    @PostMapping
    public RequestDto create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestBody RequestDto requestDto
    ) {
        Request request = RequestMapper.mapToRequest(requestDto);
        request.setCreator(userService.findById(userId));
        return RequestMapper.mapToRequestDto(requestService.create(request));
    }
}
