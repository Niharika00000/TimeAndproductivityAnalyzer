package com.productivity.analyzer.dto;

public record SummaryResponse(
        long totalTasks,
        long completedTasks,
        long inProgressTasks,
        double completionRate,
        long totalMinutesLogged,
        long minutesLast7Days,
        double avgDailyMinutes,
        int productivityScore) {
}
