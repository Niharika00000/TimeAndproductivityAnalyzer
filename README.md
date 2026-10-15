# Time & Productivity Analyzer (backend)

Spring Boot 3 · Java 17 · JPA/Hibernate · MySQL (H2 for quick runs)

## Run
**With MySQL** (DB `productivity_db` is created automatically):

    export DB_USER=root DB_PASSWORD=yourpassword
    mvn spring-boot:run

**Without MySQL** (in-memory H2):

    mvn spring-boot:run -Dspring-boot.run.profiles=h2

API: http://localhost:8080

## Endpoints
| Method | URL | Purpose |
|---|---|---|
| POST | /api/tasks | create task |
| GET | /api/tasks?status=DONE | list (optional filter) |
| GET | /api/tasks/{id} | one task |
| PUT | /api/tasks/{id} | edit task |
| PATCH | /api/tasks/{id}/status | TODO / IN_PROGRESS / DONE |
| DELETE | /api/tasks/{id} | delete task + its logs |
| POST | /api/tasks/{id}/logs | log time spent |
| GET | /api/tasks/{id}/logs | time logs of a task |
| GET | /api/analytics/summary | totals, completion rate, score |
| GET | /api/analytics/by-category | minutes per category |
| GET | /api/analytics/daily?days=7 | minutes per day (zero-filled) |

## Try it
    curl -X POST localhost:8080/api/tasks -H "Content-Type: application/json" \
      -d '{"title":"Study Spring Boot","category":"Learning","estimatedMinutes":120}'

    curl -X POST localhost:8080/api/tasks/1/logs -H "Content-Type: application/json" \
      -d '{"minutes":45,"note":"JPA chapter"}'

    curl -X PATCH localhost:8080/api/tasks/1/status -H "Content-Type: application/json" \
      -d '{"status":"DONE"}'

    curl localhost:8080/api/analytics/summary

## Productivity score
`score = 70% * completionRate + 30% * min(avgDailyMinutes(last 7d) / 240, 1)`
