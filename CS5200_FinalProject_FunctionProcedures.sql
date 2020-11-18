use mouse_housing;

DROP FUNCTION IF EXISTS login;

DELIMITER //
CREATE FUNCTION login(
	uName INT,
    pWord VARCHAR(255)
)
RETURNS INT 
DETERMINISTIC READS SQL DATA
BEGIN   
	DECLARE usercount INT;
	DECLARE admincount INT;
	SELECT COUNT(*) INTO usercount FROM `user` WHERE UserID = uName AND UserPassword = pWord AND AdminFlag = 0;
	SELECT COUNT(*) INTO admincount FROM `user` WHERE UserID = uName AND UserPassword = pWord AND AdminFlag = 1;
    
    IF (usercount = 0 AND admincount = 0) THEN 
		RETURN 0;
	ELSEIF (usercount = 1) THEN 
		RETURN 1;
	ELSEIF (admincount = 1) THEN 
		RETURN 2;
	END IF;
END//

DELIMITER ;
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_user;

DELIMITER //

CREATE PROCEDURE new_user
(
	IN userID INT,
    IN userPass VARCHAR(25),
    IN fname VARCHAR(25),
    IN lname VARCHAR(25),
    IN email VARCHAR(40),
    IN phonenum VARCHAR(11)
)
BEGIN
    INSERT INTO `user` VALUE (userID, userPass, fname, lname, email, phonenum, FALSE, NULL);
END //

DELIMITER ;

  
-- --------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_address;

DELIMITER //

CREATE PROCEDURE new_address
(
	IN user_id INT,
	IN user_straddress VARCHAR(50),
    IN user_city VARCHAR(50),
    IN user_state VARCHAR(2),
    IN user_zip VARCHAR(10)
)
BEGIN
	DECLARE pk_adr INT;
    DECLARE temp INT;
    
    -- Determine if the exact address already exists in the table
    SELECT COUNT(*) INTO temp FROM address WHERE Street = user_straddress AND City = user_city AND State = user_state AND Zip = user_zip;
    
    -- if exists then get its primary key and assign it to the new user - ie shared housing
    IF (temp > 0) THEN
		SELECT AddressIndex INTO pk_adr FROM address WHERE Street = user_straddress AND City = user_city AND State = user_state AND Zip = user_zip;
    ELSE
    	INSERT INTO address (Street, City, State, Zip) VALUE (user_straddress, user_city, user_state, user_zip);
		SELECT AddressIndex INTO pk_adr FROM address WHERE Street = user_straddress AND City = user_city AND State = user_state AND Zip = user_zip;
	END IF;
    
    UPDATE `user` SET Address = pk_adr WHERE UserID = user_id;
END //

DELIMITER ;

-- ---------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS new_facility;

DELIMITER //

CREATE PROCEDURE new_facility
(
	IN fID INT,
    IN fName VARCHAR(50)
)
BEGIN
	INSERT INTO facility VALUE (fID, fName);
END //

DELIMITER ;
