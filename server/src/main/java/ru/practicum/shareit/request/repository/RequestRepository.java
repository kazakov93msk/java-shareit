package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    List<Request> findByCreatorId(Long creatorId, Sort sort);

    Page<Request> findAllByCreatorIdIsNot(Long userId, Pageable pageable);
}
