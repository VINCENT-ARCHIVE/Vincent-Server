package com.vincent.logs.repository;

import com.vincent.logs.entity.ApiLogs;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogsRepository extends JpaRepository<ApiLogs, Long> {
    List<ApiLogs> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
