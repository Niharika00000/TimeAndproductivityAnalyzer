package com.productivity.analyzer.dto;

import java.time.LocalDate;

public record TimeLogResponse(Long id, Long taskId, int minutes, LocalDate loggedOn, String note) {
}
