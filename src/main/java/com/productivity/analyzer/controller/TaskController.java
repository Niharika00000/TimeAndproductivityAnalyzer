package com.productivity.analyzer.controller;

import com.productivity.analyzer.dto.*;
import com.productivity.analyzer.model.TaskStatus;
import com.productivity.analyzer.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<TaskResponse> list(@RequestParam(required = false) TaskStatus status) {
        return service.getAll(status);
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return service.updateStatus(id, req.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/logs")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeLogResponse logTime(@PathVariable Long id, @Valid @RequestBody TimeLogRequest req) {
        return service.logTime(id, req);
    }

    @GetMapping("/{id}/logs")
    public List<TimeLogResponse> logs(@PathVariable Long id) {
        return service.getLogs(id);
    }
}
