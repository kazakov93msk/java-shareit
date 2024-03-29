package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class RequestMapper {
    public static RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .creator(UserMapper.mapToUserDto(request.getCreator()))
                .created(request.getCreated())
                .items(ItemMapper.mapToItemDto(request.getItems(), null))
                .build();
    }

    public static Request mapToRequest(RequestDto requestDto) {
        return Request.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .creator(UserMapper.mapToUser(requestDto.getCreator()))
                .created(requestDto.getCreated())
                .build();
    }

    public static List<RequestDto> mapToRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::mapToRequestDto).collect(Collectors.toList());
    }
}
