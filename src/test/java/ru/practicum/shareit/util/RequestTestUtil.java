package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.util.ItemTestUtil.getOutputDto;
import static ru.practicum.shareit.util.UserTestUtil.*;

@UtilityClass
public class RequestTestUtil {
    public static final String REQUEST_DEFAULT_PATH = "/requests";
    public static final Long REQUEST_ID = 1L;
    public static final String REQUEST_DESCR = "Description";
    public static final Long ANOTHER_REQUEST_ID = 2L;
    public static final String REQUEST_PATH = REQUEST_DEFAULT_PATH + "/" + REQUEST_ID;

    public static Request getRequest(LocalDateTime dt) {
        return new Request(REQUEST_ID, REQUEST_DESCR, getUser(), dt, new ArrayList<>());
    }

    public static List<Request> getRequestsList(LocalDateTime dt) {
        List<Request> requests = new ArrayList<>();
        requests.add(new Request(REQUEST_ID, REQUEST_DESCR, getUser(), dt, new ArrayList<>()));
        requests.add(new Request(ANOTHER_REQUEST_ID, REQUEST_DESCR, getUser(), dt, new ArrayList<>()));
        requests.get(1).getCreator().setId(ANOTHER_USER_ID);
        return requests;
    }

    public static Page<Request> getRequestsPage(LocalDateTime dt) {
        return new PageImpl<>(getRequestsList(dt));
    }

    public static RequestDto getRequestDto(LocalDateTime dt) {
        return new RequestDto(null, REQUEST_DESCR, null, dt, List.of(getOutputDto(USER_ID)));
    }

    public static RequestDto getOutputRequestDto(LocalDateTime dt) {
        return RequestMapper.mapToRequestDto(getRequest(dt));
    }
}
