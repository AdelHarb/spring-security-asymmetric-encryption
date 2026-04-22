package com.stroheim.app.todo.impl;

import com.stroheim.app.category.Category;
import com.stroheim.app.category.CategoryRepository;
import com.stroheim.app.todo.Todo;
import com.stroheim.app.todo.TodoMapper;
import com.stroheim.app.todo.TodoRepository;
import com.stroheim.app.todo.request.TodoRequest;
import com.stroheim.app.todo.request.TodoUpdateRequest;
import com.stroheim.app.todo.response.TodoResponse;
import com.stroheim.app.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl Test")
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodoMapper todoMapper;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TodoServiceImpl todoService; //This is field of test

    private Category testCategory;
    private Todo testTodo;
    private TodoRequest todoRequest;
    private TodoUpdateRequest todoUpdateRequest;
    private TodoResponse todoResponse;

    @BeforeEach
    void setUp(){

        final User testUser = User.builder()
                .id("user-123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();


        this.testCategory = Category.builder()
                .id("category-123")
                .name("Work")
                .description("Description")
                .build();

        this.testTodo = Todo.builder()
                .id("todo-123")
                .title("Test Todo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .done(false)
                .user(testUser)
                .category(testCategory)
                .build();

        this.todoRequest = TodoRequest.builder()
                .title("Test Todo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .categoryId("category-123")
                .build();

        this.todoUpdateRequest = TodoUpdateRequest
                .builder()
                .title("Test Update")
                .description("Test Description Update")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(19, 0))
                .categoryId("category-123")
                .build();

        this.todoResponse = TodoResponse
                .builder()
                .id("todo-123")
                .title("Test Todo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .done(false)
                .build();
    }


    @Nested
    @DisplayName("Create Todo Tests")
    class CreateTodoTests {

        @Test
        @DisplayName("Should create todo successfully when valid request and category exists")
        void shouldCreateTodoSuccessfully() {

            //given
            final String userId = "user-123";
            when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId)).thenReturn(Optional.of(testCategory));
            when(todoMapper.toTodo(todoRequest)).thenReturn(testTodo);
            when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

            //when
            final String result = todoService.createTodo(todoRequest, userId);

            //then
            assertNotNull(result);
            assertEquals(testTodo.getId(), result);
            verify(categoryRepository, times(1)).findByIdAndUserId(todoRequest.getCategoryId(), userId);
            verify(todoMapper, times(1)).toTodo(todoRequest);
            verify(todoRepository, times(1)).save(testTodo);

            // verify that category is set on todo
            verify(todoRepository, times(1)).save(argThat(todo -> todo.getCategory() != null && todo.getCategory().getId().equals("category-123")));
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when category does not exist")
        void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {

            // Given
            final String userId = "user-123";

            when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
                    .thenReturn(Optional.empty());

            // When & Then

            final EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.createTodo(todoRequest, userId)
            );

            assertEquals("Not found", exception.getMessage());
            verify(todoRepository, times(1)).save(testTodo);
            verifyNoInteractions(todoMapper);
            verifyNoInteractions(todoRepository);


        }

        @Test
        @DisplayName("should handle null category ID in request")
        void shouldHandleNullCatIdInRequest() {

            // Given
            final String userId = "user-123";
            todoRequest.setCategoryId(null);

            when(categoryRepository.findByIdAndUserId(null, userId))
                    .thenReturn(Optional.empty());

            final EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.createTodo(todoRequest, userId)
            );

            assertNotNull(exception);
            assertEquals("Not found", exception.getMessage());
            verify(todoRepository, times(1)).save(testTodo);
            verifyNoInteractions(todoMapper);
            verifyNoInteractions(todoRepository);

        }
    }

    @Nested
    @DisplayName("Update Todo Tests")
    class UpdateTodoTests {

        @Test
        @DisplayName("should update successfully a todo when todo and category exists")
        void shouldSuccessfullyUpdateTodo() {

            //Given
            final String userId = "user-123";
            final String todoId = "todo-123";

            when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
            when(categoryRepository.findByIdAndUserId(testTodo.getCategory().getId(), userId)).thenReturn(Optional.of(testCategory));
            when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

            //When
            todoService.updateTodo(todoUpdateRequest, todoId, userId);

            //Then
            verify(todoRepository, times(1)).findById(todoId);
            verify(categoryRepository, times(1)).findByIdAndUserId(testTodo.getCategory().getId(), userId);
            verify(todoMapper, times(1)).mergeTodo(testTodo, todoUpdateRequest);
            verify(todoRepository, times(1)).save(testTodo);

            // verify that the todo's fields were updated correctly
            assertEquals(testCategory, testTodo.getCategory());


        }

        @Test
        @DisplayName("should throw entity not found exception when todo to update does not exist")
        void shouldThrowEntityNotFoundExceptionWhenTodoNotFound() {

            //Given
            final String userId = "user-123";
            final String todoId = "todo-123";
            when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

            final EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.updateTodo(todoUpdateRequest, todoId, userId)
            );

            assertEquals("Todo not found", exception.getMessage());
            verify(todoRepository, times(1)).findById(todoId);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(todoMapper);
            verify(todoRepository, never()).save(any());

        }

        @Test
        @DisplayName("should throw entity not found exception when category not exist")
        void shouldThrowEntityNotFoundExceptionWhenCategoryFound() {

            //Given
            final String userId = "user-123";
            final String todoId = "todo-123";
            when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
            when(categoryRepository.findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId)).thenReturn(Optional.empty());


            final EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.updateTodo(todoUpdateRequest, todoId, userId)
            );

            assertEquals("No category was found for that user with id" + todoUpdateRequest.getCategoryId(), exception.getMessage());
            verify(todoRepository, times(1)).findById(todoId);
            verify(categoryRepository).findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId);
            verifyNoInteractions(todoMapper);
            verify(todoRepository, never()).save(any());

        }

    }

    @Nested
    @DisplayName("Find Todo By ID)")
    class FindTodoByIdTests {

        @Test
        @DisplayName("should return todo response when todo exists")
        void shouldReturnTodoResponse() {

            //Given
            final String todoId = "user-123";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.of(testTodo));
            when(todoMapper.toTodoResponse(testTodo))
                    .thenReturn(todoResponse);

            //When
            final TodoResponse result = todoService.findTodoById(todoId);

            //Then
            assertNotNull(todoResponse);
            assertEquals(todoResponse, result);
            verify(todoRepository, times(1)).findById(todoId);
            verify(todoMapper).toTodoResponse(testTodo);

        }

        @Test
        @DisplayName("should throw entity not found exception when todo not found")
        void shouldThrowEntityNotFoundExceptionWhenTodoNotFound() {

            //Given
            final String todoId = "non-existing-todo-123";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.empty());

            //When & Then
            final EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.findTodoById(todoId)
            );

            assertEquals("No todo found with Id" + todoId, exception.getMessage());
            verify(todoRepository, times(1)).findById(todoId);
            verifyNoInteractions(todoMapper);


        }

        @Test
        @DisplayName("should handle null todo Id")
        void shouldHandleNullId() {

            //Given
            when(todoRepository.findById(null))
                    .thenReturn(Optional.empty());

            //When & Then
            final EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.findTodoById(null)
            );

            assertEquals("No todo found with id null", exception.getMessage());
            verify(todoRepository, times(1)).findById(null);
            verifyNoInteractions(todoMapper);


        }

    }

}