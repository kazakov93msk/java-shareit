package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    List<Comment> findByItemId(Long itemId, Sort sort);
    List<Comment> findByItemIn(List<Item> items, Sort sort);
}
