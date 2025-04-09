-- CHECK stadium is free before 2 hours and after 2 hours of the match time will be added
DELIMITER //
DROP PROCEDURE IF EXISTS up_CheckStadiumFree //
CREATE PROCEDURE up_CheckStadiumFree(
    IN pStadiumId INT,
    IN pMatchDate VARCHAR(255),
    IN pMatchTime VARCHAR(255)
)
BEGIN
    SELECT EXISTS(SELECT 1
                  FROM matches
                  WHERE stadium_id = pStadiumId
                    AND match_date = pMatchDate
                    AND match_time BETWEEN ADDTIME(pMatchTime, '-2:00:00') AND ADDTIME(pMatchTime, '2:00:00'));
end //
