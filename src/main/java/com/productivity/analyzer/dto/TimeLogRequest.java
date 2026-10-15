package com.productivity.analyzer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** loggedOn is optional; defaults to today. */
public record TimeLogRequest(
        @Min(1) @Max(1440) int minutes,
        LocalDate loggedOn,
        @Size(max = 255) String note) {
}
