package com.stroheim.app.todo;

import com.stroheim.app.todo.response.TodoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {

    @Query("""
            SELECT t from Todo t
            WHERE t.user.id = :userId
                        AND t.startDate = CURRENT_DATE
                       """)
    List<Todo> findAllByUserId(String userId);

    List<Todo> findAllByUserIdAndCategoryId(String userId, String categoryId);

    @Query("""
            SELECT t from Todo t
            WHERE t.endDate >= CURRENT_DATE AND t.endTime <= CURRENT_TIME
                       """)
    List<Todo> findAllDueTodos(String userId);
}
