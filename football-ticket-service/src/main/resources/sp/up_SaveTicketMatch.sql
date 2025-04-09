DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveTicketMatch //
CREATE PROCEDURE up_SaveTicketMatch(
    IN pMatchId INT,
    IN pTypeId INT,
    IN pPrice DECIMAL(10, 2),
    IN pQuantity INT,
    IN pDescription TEXT
)
BEGIN
    INSERT INTO tickets(match_id, type_id, price,  description)
    VALUES (pMatchId, pTypeId, pPrice, pDescription)
    ON DUPLICATE KEY UPDATE price       = pPrice,
                            description = pDescription;
END
//

