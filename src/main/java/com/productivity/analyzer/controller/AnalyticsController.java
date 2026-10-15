package com.productivity.analyzer.controller;

import com.productivity.analyzer.dto.CategoryMinutes;
import com.productivity.analyzer.dto.DailyMinutes;
import com.productivity.analyzer.dto.SummaryResponse;
import com.productivity.analyzer.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/summary")
    public SummaryResponse summary() {
        return service.summary();
    }

    @GetMapping("/by-category")
    public List<CategoryMinutes> byCategory() {
        return service.byCategory();
    }

    @GetMapping("/daily")
    public List<DailyMinutes> daily(@RequestParam(defaultValue = "7") int days) {
        return service.daily(days);
    }
}
