package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    Page<Todo> findByTitleAndNickName(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String title, String nickName, Pageable pageable);
}
