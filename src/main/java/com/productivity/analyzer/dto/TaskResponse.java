package com.productivity.analyzer.dto;

import com.productivity.analyzer.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        String category,
        TaskStatus status,
        int estimatedMinutes,
        long spentMinutes,
        LocalDateTime createdAt,
        LocalDateTime completedAt) {
}
