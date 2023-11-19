package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findAllByAssigneeId(Long assigneeId, Sort sort);

}
