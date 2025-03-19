package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId)).fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Todo> findByTitleAndNickName(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String title, String nickName, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 생성일 검색
        if (createdAtStart != null && createdAtEnd != null) {                    //  시작일과 종료일이 모두 입력된 경우
            builder.and(todo.createdAt.between(createdAtStart, createdAtEnd));
        } else if (createdAtStart != null && createdAtEnd == null) {            //  시작일만 입력된 경우
            builder.and(todo.createdAt.goe(createdAtStart));
        } else if (createdAtStart == null && createdAtEnd != null) {            //  종료일만 입력된 경우
            builder.and(todo.createdAt.loe(createdAtEnd));
        }

        // 제목 검색
        if (title != null) {
            builder.and(todo.title.containsIgnoreCase(title));
        }

        // 닉네임 검색
        if(nickName != null){
            builder.and(todo.user.nickName.containsIgnoreCase(nickName));
        }

        List<Todo> result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.comments, comment).fetchJoin()
                .leftJoin(todo.managers, manager).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 수
        JPAQuery<Long> count = queryFactory
                .select(todo.count())
                .from(todo)
                .where(builder);

        // 데이터 수가 페이지 사이즈보다 작을 경우 데이터 수 조회 쿼리 실행 안함
        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }
}
