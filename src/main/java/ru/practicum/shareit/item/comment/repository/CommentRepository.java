package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select new ru.practicum.shareit.item.comment.dto.CommentDto(c.id, c.text, c.author.name, c.created) " +
            "from Comment c " +
            "where c.item.id = ?1 " +
            "order by c.created")
    List<CommentDto> findDtoCommentsByItemId(Long itemId);

}
