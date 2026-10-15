package com.productivity.analyzer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank @Size(max = 150) String title,
        @Size(max = 500) String description,
        @NotBlank @Size(max = 80) String category,
        @Min(0) int estimatedMinutes) {
}
