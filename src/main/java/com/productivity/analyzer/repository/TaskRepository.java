package com.productivity.analyzer.repository;

import com.productivity.analyzer.model.Task;
import com.productivity.analyzer.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    long countByStatus(TaskStatus status);
}
