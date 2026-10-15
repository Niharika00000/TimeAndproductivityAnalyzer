package com.productivity.analyzer.dto;

import java.time.LocalDate;

public record DailyMinutes(LocalDate date, long minutes) {
}
