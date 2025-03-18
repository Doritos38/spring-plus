package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE (:weather IS NULL OR t.weather = :weather)")
    Page<Todo> findByWeather(String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.modifiedAt >= :modifiedAtStart AND (:weather IS NULL OR t.weather = :weather)")
    Page<Todo> findByUpdateAtAfter(LocalDateTime modifiedAtStart, String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.modifiedAt <= :modifiedAtEnd AND (:weather IS NULL OR t.weather = :weather)")
    Page<Todo> findByUpdateAtBefore(LocalDateTime modifiedAtEnd, String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.modifiedAt BETWEEN :modifiedAtStart AND :modifiedAtEnd AND (:weather IS NULL OR t.weather = :weather)")
    Page<Todo> findByModifiedAtBetween(LocalDateTime modifiedAtStart, LocalDateTime modifiedAtEnd, String weather, Pageable pageable);
}
