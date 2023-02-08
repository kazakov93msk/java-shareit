package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;

import static ru.practicum.shareit.utility.RequestUtil.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;
    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(HEADER_USER_ID) Long userId) {
        
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long requestId
    ) {
        
        return requestClient.findById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "1") Long from,
            @RequestParam(defaultValue = "30") Integer size
    ) {
        
        return requestClient.findAll(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestBody @Valid RequestDto requestDto
    ) {
        return requestClient.create(userId, requestDto);
    }
}
