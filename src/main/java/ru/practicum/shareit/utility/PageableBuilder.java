package ru.practicum.shareit.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.IllegalPageArgumentException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;

@UtilityClass
public class PageableBuilder {
    public static final Integer DEFAULT_SIZE = 30;
    public static final Long DEFAULT_START = 1L;

    public static Pageable getPageable(Long start, Integer size, Sort sort) {
        if (start == null) {
            start = DEFAULT_START;
        } else if (start < 0) {
            throw new IllegalPageArgumentException("Parameter 'start' cannot be less than 0.");
        }

        if (size == null) {
            size = DEFAULT_SIZE;
        } else if (size < 0) {
            throw new IllegalPageArgumentException("Parameter 'size' cannot be less than 0.");
        }

        if (sort == null) {
            throw new IllegalPageArgumentException("Parameter 'sort' cannot be is null.");
        }

        int pages = Math.toIntExact(start / size);
        return PageRequest.of(pages, size, sort);
    }

    public static Pageable getItemDefaultPageable() {
        return getPageable(DEFAULT_START, DEFAULT_SIZE, ItemRepository.ID_ASC);
    }

    public static Pageable getRequestDefaultPageable() {
        return getPageable(DEFAULT_START, DEFAULT_SIZE, RequestRepository.CREATED_DESC);
    }
}
