package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findAllByAssigneeId(Long assigneeId);
    List<Task> findAllByAssigneeId(Long assigneeId, Sort sort);
    List<Task> findAllByAuthorId(Long authorId);

//    @Query("select c from Car c where c.status <> '2'")
//    List<Car> findAllNotDeleted(Pageable request);
//
//    @Query("select c from Car c where c.status <> '2' and  (c.brand like %:filter% or c.model like %:filter%)")
//    List<Car> findAllNotDeleted(Pageable request, @Param("filter") String filter);
}
