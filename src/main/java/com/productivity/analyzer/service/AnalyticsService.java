package com.productivity.analyzer.service;

import com.productivity.analyzer.dto.CategoryMinutes;
import com.productivity.analyzer.dto.DailyMinutes;
import com.productivity.analyzer.dto.SummaryResponse;
import com.productivity.analyzer.model.TaskStatus;
import com.productivity.analyzer.repository.TaskRepository;
import com.productivity.analyzer.repository.TimeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private static final double DAILY_FOCUS_TARGET_MINUTES = 240.0; // 4h/day = full focus marks

    private final TaskRepository taskRepo;
    private final TimeLogRepository logRepo;

    public SummaryResponse summary() {
        long total = taskRepo.count();
        long done = taskRepo.countByStatus(TaskStatus.DONE);
        long inProgress = taskRepo.countByStatus(TaskStatus.IN_PROGRESS);
        double completionRate = total == 0 ? 0 : round1(done * 100.0 / total);

        long totalMinutes = logRepo.totalMinutes();
        long last7 = logRepo.minutesSince(LocalDate.now().minusDays(6));
        double avgDaily = round1(last7 / 7.0);

        // Score = 70% task completion + 30% consistency of daily focused time.
        double focus = Math.min(avgDaily / DAILY_FOCUS_TARGET_MINUTES, 1.0);
        int score = (int) Math.round(Math.min(100, completionRate * 0.7 + focus * 30));

        return new SummaryResponse(total, done, inProgress, completionRate,
                totalMinutes, last7, avgDaily, score);
    }

    public List<CategoryMinutes> byCategory() {
        return logRepo.minutesByCategory().stream()
                .map(c -> new CategoryMinutes(c.getCategory(), c.getMinutes()))
                .toList();
    }

    /** Last N days (1-90), with zero-filled days so charts have no gaps. */
    public List<DailyMinutes> daily(int days) {
        int n = Math.max(1, Math.min(days, 90));
        LocalDate from = LocalDate.now().minusDays(n - 1L);

        Map<LocalDate, Long> found = logRepo.minutesByDay(from).stream()
                .collect(Collectors.toMap(TimeLogRepository.DailyTime::getDay,
                                          TimeLogRepository.DailyTime::getMinutes));

        return IntStream.range(0, n)
                .mapToObj(from::plusDays)
                .map(d -> new DailyMinutes(d, found.getOrDefault(d, 0L)))
                .toList();
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
