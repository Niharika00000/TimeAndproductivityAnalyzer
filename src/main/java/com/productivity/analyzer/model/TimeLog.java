package com.productivity.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "time_logs", indexes = {
        @Index(name = "idx_log_date", columnList = "logged_on"),
        @Index(name = "idx_log_task", columnList = "task_id")
})
@Getter
@Setter
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(nullable = false)
    private int minutes;

    @Column(name = "logged_on", nullable = false)
    private LocalDate loggedOn;

    @Column(length = 255)
    private String note;
}
