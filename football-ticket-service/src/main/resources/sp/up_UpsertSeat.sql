DELIMITER //
DROP PROCEDURE IF EXISTS up_UpsertSeat //
CREATE PROCEDURE up_UpsertSeat(
    IN pSeatId INT,
    IN pSeatNumber VARCHAR(255),
    IN pStadiumId INT,
    IN pTypeId INT
)
BEGIN
    IF (SELECT EXISTS(SELECT 1 FROM seat WHERE seat_id = pSeatId)) THEN
        UPDATE seat
        set seat_number = pSeatNumber,
            stadium_id  = pStadiumId,
            type_id     = pTypeId
        WHERE seat_id = pSeatId;
    ELSE
        INSERT INTO seat (seat_number, stadium_id, type_id)
        VALUES (pSeatNumber, pStadiumId, pTypeId);
    END IF;
END
//

