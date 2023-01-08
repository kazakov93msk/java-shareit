package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.dto.RequestDto;

@Mapper
public class RequestMapper {
    public static RequestDto toItemRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreator(),
                request.getCreated()
        );
    }

    public static Request toItemRequest(RequestDto requestDto) {
        return new Request(
                requestDto.getId(),
                requestDto.getDescription(),
                requestDto.getCreator(),
                requestDto.getCreated()
        );
    }
}
