package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TodoSearchResponse {

    private final Long id;
    private final String title;
    private final Long managerCount;
    private final Long commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

//    public TodoSearchResponse(Long id, String title, Long managerCount, Long commentCount, LocalDateTime createdAt, LocalDateTime modifiedAt) {
//        this.id = id;
//        this.title = title;
//        this.managerCount = managerCount;
//        this.commentCount = commentCount;
//        this.createdAt = createdAt;
//        this.modifiedAt = modifiedAt;
//    }
}
