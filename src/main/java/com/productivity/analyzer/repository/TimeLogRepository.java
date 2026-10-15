package com.productivity.analyzer.repository;

import com.productivity.analyzer.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    // Projections: aggregation is done in the database, not in Java.
    interface CategoryTime {
        String getCategory();
        Long getMinutes();
    }

    interface DailyTime {
        LocalDate getDay();
        Long getMinutes();
    }

    interface TaskTime {
        Long getTaskId();
        Long getMinutes();
    }

    List<TimeLog> findByTaskIdOrderByLoggedOnDesc(Long taskId);

    @Query("select coalesce(sum(l.minutes), 0) from TimeLog l")
    long totalMinutes();

    @Query("select coalesce(sum(l.minutes), 0) from TimeLog l where l.loggedOn >= :from")
    long minutesSince(@Param("from") LocalDate from);

    @Query("select coalesce(sum(l.minutes), 0) from TimeLog l where l.task.id = :taskId")
    long minutesForTask(@Param("taskId") Long taskId);

    @Query("select l.task.id as taskId, sum(l.minutes) as minutes from TimeLog l group by l.task.id")
    List<TaskTime> minutesPerTask();

    @Query("select l.task.category as category, sum(l.minutes) as minutes " +
           "from TimeLog l group by l.task.category order by sum(l.minutes) desc")
    List<CategoryTime> minutesByCategory();

    @Query("select l.loggedOn as day, sum(l.minutes) as minutes " +
           "from TimeLog l where l.loggedOn >= :from group by l.loggedOn order by l.loggedOn")
    List<DailyTime> minutesByDay(@Param("from") LocalDate from);

    @Modifying
    @Query("delete from TimeLog l where l.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") Long taskId);
}
