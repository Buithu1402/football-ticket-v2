DELIMITER //
DROP EVENT IF EXISTS ev_UpdateMatchStatus //
CREATE EVENT IF NOT EXISTS ev_UpdateMatchStatus
    ON SCHEDULE EVERY 5 MINUTE
    DO
    BEGIN
        -- Cập nhật trạng thái "live" nếu trận đấu chưa quá 2 giờ -- 90p + break time = 2 hours
        UPDATE matches
        SET status = 'live'
        WHERE status = 'pending'
          AND TIMESTAMP(match_date, match_time) <= NOW()
          AND TIMESTAMP(match_date, match_time) > NOW() - INTERVAL 2 HOUR;

        -- Cập nhật trạng thái "completed" nếu trận đấu đã quá 2 giờ
        UPDATE matches
        SET status = 'completed'
        WHERE status <> 'completed'
          AND TIMESTAMP(match_date, match_time) <= NOW() - INTERVAL 2 HOUR;
    END
//

DELIMITER ;
