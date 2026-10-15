package com.productivity.analyzer.service;

import com.productivity.analyzer.dto.*;
import com.productivity.analyzer.exception.ResourceNotFoundException;
import com.productivity.analyzer.model.Task;
import com.productivity.analyzer.model.TaskStatus;
import com.productivity.analyzer.model.TimeLog;
import com.productivity.analyzer.repository.TaskRepository;
import com.productivity.analyzer.repository.TimeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final TimeLogRepository logRepo;

    @Transactional
    public TaskResponse create(TaskRequest req) {
        Task task = new Task();
        apply(task, req);
        return toResponse(taskRepo.save(task), 0L);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll(TaskStatus status) {
        List<Task> tasks = (status == null)
                ? taskRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                : taskRepo.findByStatusOrderByCreatedAtDesc(status);

        // One grouped query instead of one query per task (avoids N+1).
        Map<Long, Long> spent = logRepo.minutesPerTask().stream()
                .collect(Collectors.toMap(TimeLogRepository.TaskTime::getTaskId,
                                          TimeLogRepository.TaskTime::getMinutes));

        return tasks.stream()
                .map(t -> toResponse(t, spent.getOrDefault(t.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = find(id);
        return toResponse(task, logRepo.minutesForTask(id));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest req) {
        Task task = find(id);
        apply(task, req);
        return toResponse(task, logRepo.minutesForTask(id));
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        Task task = find(id);
        task.setStatus(status);
        task.setCompletedAt(status == TaskStatus.DONE ? LocalDateTime.now() : null);
        return toResponse(task, logRepo.minutesForTask(id));
    }

    @Transactional
    public void delete(Long id) {
        Task task = find(id);
        logRepo.deleteByTaskId(id);
        taskRepo.delete(task);
    }

    @Transactional
    public TimeLogResponse logTime(Long taskId, TimeLogRequest req) {
        Task task = find(taskId);

        TimeLog log = new TimeLog();
        log.setTask(task);
        log.setMinutes(req.minutes());
        log.setLoggedOn(req.loggedOn() != null ? req.loggedOn() : LocalDate.now());
        log.setNote(req.note());
        log = logRepo.save(log);

        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        return toLogResponse(log);
    }

    @Transactional(readOnly = true)
    public List<TimeLogResponse> getLogs(Long taskId) {
        find(taskId);
        return logRepo.findByTaskIdOrderByLoggedOnDesc(taskId).stream()
                .map(this::toLogResponse)
                .toList();
    }

    // ---- helpers ----
    private Task find(Long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    private void apply(Task task, TaskRequest req) {
        task.setTitle(req.title().trim());
        task.setDescription(req.description());
        task.setCategory(req.category().trim());
        task.setEstimatedMinutes(req.estimatedMinutes());
    }

    private TaskResponse toResponse(Task t, long spent) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getCategory(),
                t.getStatus(), t.getEstimatedMinutes(), spent, t.getCreatedAt(), t.getCompletedAt());
    }

    private TimeLogResponse toLogResponse(TimeLog l) {
        return new TimeLogResponse(l.getId(), l.getTask().getId(), l.getMinutes(), l.getLoggedOn(), l.getNote());
    }
}
