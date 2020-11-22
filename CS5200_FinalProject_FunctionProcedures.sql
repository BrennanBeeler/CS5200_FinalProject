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
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
		END;
        
	START TRANSACTION;
    
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
    
    COMMIT;
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

-- ------------------------------------------------------------------
DROP PROCEDURE IF EXISTS user_view_user;

DELIMITER //

CREATE PROCEDURE user_view_user()
BEGIN
	SELECT UserID, FirstName, LastName FROM `user`;
END //

DELIMITER ;


-- ----------------------------------------------------------------
DROP PROCEDURE IF EXISTS admin_view_user;

DELIMITER //

CREATE PROCEDURE admin_view_user()
BEGIN
	SELECT UserID, UserPassword, FirstName, LastName, Email, PhoneNum, AdminFlag, CONCAT(a.Street, " ", a.City, " ", a.State, " ", a.Zip) AS "Address" FROM `user` AS u
    LEFT OUTER JOIN address AS a
    ON u.Address = a.AddressIndex;
END //

DELIMITER ;


-- ----------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_room;

DELIMITER //

CREATE PROCEDURE new_room
(
	IN fID INT,
    IN rNum VARCHAR(10),
    IN lCycle VARCHAR(5)
)
BEGIN
	INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (fID, rNum, lCycle);
END //

DELIMITER ;

-- ----------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_rack;

DELIMITER //

CREATE PROCEDURE new_rack
(
	IN raID INT,
    IN CageSlots INT,
    IN rmID INT
)
BEGIN
	INSERT INTO rack VALUE (raID, CageSlots, 0, rmID);
END //

DELIMITER ;

-- ----------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_cage;

DELIMITER //

CREATE PROCEDURE new_cage
(
	IN cID INT,
    IN bType VARCHAR(20),
    IN cStatus VARCHAR(8),
    IN rID INT,
    IN manID INT,
    IN breed BOOLEAN
)
BEGIN
	INSERT INTO cage VALUE (cID, bType, cStatus, rID, manID, breed);
END //

DELIMITER ;

-- ----------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_mouse;

DELIMITER //

CREATE PROCEDURE new_mouse
(
	IN eTag INT,
    IN geno VARCHAR(10),
    IN sx VARCHAR(1),
    IN dob DATE,
    IN dod DATE,
    IN cID INT,
    IN originID INT,
    IN manID INT
)
BEGIN
	-- IF (EXISTS (SELECT CageID FROM cage WHERE Manager = manID AND CageID = cID))
-- 	AND ((originID AND cID) IN (SELECT CageID FROM cage)) AND cID != originID THEN
-- 		INSERT INTO mouse VALUE (eTag, geno, sx, dob, dod, cID, originID);
-- 	END IF;

		
	
IF NOT EXISTS (SELECT CageID FROM cage WHERE Manager = manID AND CageID = cID) THEN
	SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = "Cannot add mouse to cage of another user."; -- make sure not causing admin to see this
ELSEIF (cID) NOT IN (SELECT CageID FROM cage) THEN
	SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = "Cannot add mouse to cage that doens't exist.";
ELSEIF (cID != originID ) THEN
	SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = "Cannot have same cage and origin cage.";
ELSE
	INSERT INTO mouse VALUE (eTag, geno, sx, dob, dod, cID, originID);
END IF;

	
END //

DELIMITER ;

-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS insert_mouse_cage_limit;

DELIMITER //

CREATE TRIGGER insert_mouse_cage_limit
	BEFORE INSERT ON mouse
    FOR EACH ROW
BEGIN 
	IF EXISTS(SELECT CageID FROM cage AS c WHERE c.Breeding = FALSE AND c.CageID = NEW.CageID) THEN 
		IF (SELECT COUNT(*) FROM mouse WHERE CageID = NEW.CageID) >= 5 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "Cannot house more than 5 mice in a cage.";
		END IF;
	ELSE 
		IF (SELECT COUNT(*) FROM mouse AS m2 WHERE NEW.Sex = m2.Sex AND NEW.Sex = 'M' AND NEW.CageID = m2.CageID) >= 1 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "Already a male breeder in this cage.";
		ELSEIF (SELECT COUNT(*) FROM mouse AS m2 WHERE NEW.Sex = m2.Sex AND NEW.Sex = 'F' AND NEW.CageID = m2.CageID) >= 1 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "Already a female breeder in this cage.";
		END IF;
	END IF;
		
END //
DELIMITER ;

-- ----------------------------------------------------------------
DROP PROCEDURE IF EXISTS new_genotype;

DELIMITER //

CREATE PROCEDURE new_genotype
(
	IN genoabr VARCHAR(10),
    IN genofull VARCHAR(200)
)
BEGIN
	INSERT INTO genotype VALUE(genoabr, genofull);
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_genotype;

DELIMITER //

CREATE PROCEDURE view_genotype()
BEGIN
	SELECT * FROM genotype;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_address;

DELIMITER //

CREATE PROCEDURE update_address
(
	IN uID INT,
    IN str VARCHAR(50),
    IN city VARCHAR(50),
    IN state VARCHAR(2),
    IN zip VARCHAR(10)
)
BEGIN
	DECLARE adrID INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
            RESIGNAL;
		END;
        
	START TRANSACTION;
    
	SELECT Address INTO adrID FROM `user` WHERE UserID = uID;
    
    IF (SELECT COUNT(*) FROM `user` WHERE adrID = Address) > 1 THEN
		-- Multiple people at address
        INSERT INTO address (Street, City, State, Zip) VALUE(str, city, state, zip);
        SELECT AddressIndex INTO adrID FROM address AS a WHERE a.Street = str AND a.City = city AND a.State = state AND a.Zip = zip;
        UPDATE `user` SET Address = adrID WHERE UserID = uID; 
	ELSE
		UPDATE address AS a SET a.Street = str, a.City = city, a.State = state, a.Zip = zip WHERE AddressIndex = adrID; 
    END IF;
    
    COMMIT;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_address;

DELIMITER //

CREATE PROCEDURE view_address
(
	IN uID INT
)
BEGIN
	SELECT CONCAT(Street, " ", City, " ", State, " ", Zip) AS "Address" FROM address WHERE AddressIndex = (SELECT Address FROM `user` WHERE UserID = uID);
END //

DELIMITER ;






