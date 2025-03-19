package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate modifiedAtStart, LocalDate modifiedAtEnd) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));

        Page<Todo> todos;

        // 날짜 조건 입력에 따라 페이징
        if (modifiedAtStart == null && modifiedAtEnd == null) {          // 둘 다 null
            if(weather == null){
                todos = todoRepository.findAll(pageable);
            }else{
                todos = todoRepository.findByWeather(weather, pageable);
            }
        } else if (modifiedAtStart != null && modifiedAtEnd == null) {   // modifiedAtEnd만 null
            todos = todoRepository.findByUpdateAtAfter(modifiedAtStart.atStartOfDay(), weather, pageable);
        } else if (modifiedAtStart == null && modifiedAtEnd != null) { // modifiedAtStart만 null
            todos = todoRepository.findByUpdateAtBefore(modifiedAtEnd.atStartOfDay(), weather, pageable);
        } else {                                                    // 둘 다 null이 아님
            if (modifiedAtStart.isAfter(modifiedAtEnd)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is bigger then end date");
            }
            todos = todoRepository.findByModifiedAtBetween(modifiedAtStart.atStartOfDay(), modifiedAtEnd.atStartOfDay(), weather, pageable);
        }


        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public Page<TodoSearchResponse> searchTodos(int page, int size, String title, LocalDate createdAtStart, LocalDate createdAtEnd, String nickName) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Todo> todos = todoRepository.findByTitleAndNickName(createdAtStart.atStartOfDay(), createdAtEnd.atStartOfDay(), title, nickName, pageable);

        return todos.map(todo -> new TodoSearchResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getManagers().size(),
                todo.getComments().size(),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }
}
