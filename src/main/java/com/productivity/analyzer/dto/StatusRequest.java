package com.productivity.analyzer.dto;

import com.productivity.analyzer.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull TaskStatus status) {
}
