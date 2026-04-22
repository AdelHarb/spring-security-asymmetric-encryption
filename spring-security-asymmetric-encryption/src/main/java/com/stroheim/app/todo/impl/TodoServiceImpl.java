package com.stroheim.app.todo.impl;

import com.stroheim.app.todo.TodoService;

import com.stroheim.app.category.Category;
import com.stroheim.app.category.CategoryRepository;
import com.stroheim.app.todo.Todo;
import com.stroheim.app.todo.TodoMapper;
import com.stroheim.app.todo.TodoRepository;
import com.stroheim.app.todo.TodoService;
import com.stroheim.app.todo.request.TodoRequest;
import com.stroheim.app.todo.request.TodoUpdateRequest;
import com.stroheim.app.todo.response.TodoResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public String createTodo(TodoRequest request, String userId) {

        final Category category = checkAndReturnCategory(request.getCategoryId(), userId);
        final Todo todo = this.todoMapper.toTodo(request);
        todo.setCategory(category);
        return this.todoRepository.save(todo).getId();
    }

    @Override
    public void updateTodo(TodoUpdateRequest request, String todoId, String userId) {

        final Todo todoToUpdate = this.todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));
        final Category category = checkAndReturnCategory(request.getCategoryId(), userId);

        this.todoMapper.mergeTodo(todoToUpdate, request);
        this.todoRepository.save(todoToUpdate);
    }

    @Override
    public TodoResponse findTodoById(String todoId) {
        return this.todoRepository.findById(todoId)
                .map(this.todoMapper::toTodoResponse)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));
    }

    @Override
    public List<TodoResponse> findAllTodosForToday(String userId) {
        return mapToResponse(todoRepository.findAllByUserId(userId));
    }

    @Override
    public List<TodoResponse> findAllTodosByCategory(String catId, String userId) {
        return mapToResponse(
                todoRepository.findAllByUserIdAndCategoryId(userId, catId)
        );
    }

    @Override
    public List<TodoResponse> findAllDueTodos(String userId) {
        return mapToResponse(todoRepository.findAllDueTodos(userId));
    }


    @Override
    public void deleteTodoById(String todoId) {
        this.todoRepository.deleteById(todoId);

    }

    private Category checkAndReturnCategory(final String categoryId, final String userId) {
        return this.categoryRepository
                .findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    private List<TodoResponse> mapToResponse(List<Todo> todos) {
        return todos
                .stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

}
