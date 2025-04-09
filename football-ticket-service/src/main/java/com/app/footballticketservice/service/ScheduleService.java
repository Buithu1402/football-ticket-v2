package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ScheduleService {
    private final NamedParameterJdbcTemplate writeDb;

    public ScheduleService(@WriteDB NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    @Scheduled(cron = "0 */15 * * * *") // Runs every 15 minutes
    public void runScheduledTask() {
        var sql = """
                    UPDATE matches
                    SET status = 'pending'
                    WHERE TIMESTAMP(match_date, match_time) > NOW() + INTERVAL 2 HOUR;
                """;
        writeDb.update(sql, new HashMap<>());
    }
}
