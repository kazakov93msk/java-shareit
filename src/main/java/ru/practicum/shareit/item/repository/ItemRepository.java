package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Sort ID_ASC = Sort.by(Sort.Direction.ASC, "id");

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query(" select i from Item as i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "   and i.available = true")
    Page<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIn(List<Request> requests);
}
