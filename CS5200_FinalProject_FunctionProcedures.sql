use mouse_housing;

DROP PROCEDURE IF EXISTS login;

DELIMITER //
-- Determines user access level
CREATE PROCEDURE login(
	OUT val INT,
	IN uID INT,
    IN pWord VARCHAR(25)
)
BEGIN   
	DECLARE usercount INT;
	DECLARE admincount INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
            RESIGNAL;
		END;
        
    START TRANSACTION;    
        
	SELECT COUNT(*) INTO usercount FROM `user` WHERE UserID = uID AND UserPassword = pWord AND AdminFlag = 0;
	SELECT COUNT(*) INTO admincount FROM `user` WHERE UserID = uID AND UserPassword = pWord AND AdminFlag = 1;
    
    IF (usercount = 0 AND admincount = 0) THEN 
		SELECT 0 INTO val;
	ELSEIF (usercount = 1) THEN 
		SELECT 1 INTO val;
	ELSEIF (admincount = 1) THEN 
		SELECT 2 INTO val;
	END IF;
    
    COMMIT;
    
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
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
            RESIGNAL;
		END;
        
    IF (SELECT Address FROM `user` WHERE UserID = user_id) IS NULL THEN
    
		START TRANSACTION;
        
		-- if exists then get its primary key and assign it to the new user - ie shared housing
		IF ((SELECT COUNT(*) FROM address WHERE Street = user_straddress AND City = user_city AND State = user_state AND Zip = user_zip) > 0) THEN
			SELECT AddressIndex INTO pk_adr FROM address WHERE Street = user_straddress AND City = user_city AND State = user_state AND Zip = user_zip;
		ELSE
			INSERT INTO address (Street, City, State, Zip) VALUE (user_straddress, user_city, user_state, user_zip);
			SELECT AddressIndex INTO pk_adr FROM address WHERE Street = user_straddress AND City = user_city AND State = user_state AND Zip = user_zip;
		END IF;
		
		UPDATE `user` SET Address = pk_adr WHERE UserID = user_id;
		
		COMMIT;
        
	ELSE 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: This user already has an address.";
    END IF;
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


IF (cID) NOT IN (SELECT CageID FROM cage) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot add mouse to cage that doens't exist.";
ELSEIF (cID = originID ) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot have same cage and origin cage.";
ELSE 
	IF manID IS NOT NULL THEN
		IF NOT EXISTS (SELECT CageID FROM cage WHERE Manager = manID AND CageID = cID) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot add mouse to cage of another user.";
		ELSE
			INSERT INTO mouse VALUE (eTag, geno, sx, dob, dod, cID, originID);
		END IF;
	ELSE 
		INSERT INTO mouse VALUE (eTag, geno, sx, dob, dod, cID, originID);
	END IF;
END IF;

	
END //

DELIMITER ;

-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS insert_mouse_origin;

DELIMITER //

-- Make sure that origin cage of a mouse is breeding cage
CREATE TRIGGER insert_mouse_origin
	BEFORE INSERT ON mouse
    FOR EACH ROW
BEGIN 
	IF (NEW.OriginCage NOT IN (SELECT CageID FROM cage WHERE Breeding = true)) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Origin cage must be a breeding cage.";
	END IF;
END //
DELIMITER ;



-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS insert_mouse_cage_limit;

DELIMITER //

-- Designed to keep only 1 male and 1 female to a breeding cage, and only 5 mice to non-breeding cage
CREATE TRIGGER insert_mouse_cage_limit
	BEFORE INSERT ON mouse
    FOR EACH ROW
BEGIN 
	IF EXISTS(SELECT CageID FROM cage AS c WHERE c.Breeding = FALSE AND c.CageID = NEW.CageID) THEN 
		IF (SELECT COUNT(*) FROM mouse WHERE CageID = NEW.CageID) >= 5 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot house more than 5 mice in a cage.";
		END IF;
	ELSE 
		IF (SELECT COUNT(*) FROM mouse AS m2 WHERE NEW.Sex = m2.Sex AND NEW.Sex = 'M' AND NEW.CageID = m2.CageID) >= 1 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Already a male breeder in this cage.";
		ELSEIF (SELECT COUNT(*) FROM mouse AS m2 WHERE NEW.Sex = m2.Sex AND NEW.Sex = 'F' AND NEW.CageID = m2.CageID) >= 1 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Already a female breeder in this cage.";
		END IF;
	END IF;
		
END //
DELIMITER ;


-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS update_mouse_cage_limit;

DELIMITER //

-- Designed to keep only 1 male and 1 female to a breeding cage, and only 5 mice to non-breeding cage
CREATE TRIGGER update_mouse_cage_limit
	BEFORE UPDATE ON mouse
    FOR EACH ROW
BEGIN 
	-- Make sure not putting mouse in origin cage
	IF (NEW.CageID = NEW.OriginCage) THEN
    	SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot have same cage and origin cage.";
	-- If not a breeder cage
	ELSEIF EXISTS(SELECT CageID FROM cage AS c WHERE c.Breeding = FALSE AND c.CageID = NEW.CageID) THEN 
		-- Check if there are more than 5 mice in new cage AND check if mouse is already in cage
		IF ((SELECT COUNT(*) FROM mouse WHERE CageID = NEW.CageID) >= 5) AND (NEW.Eartag NOT IN (SELECT Eartag FROM mouse WHERE CageID = NEW.CageID)) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot house more than 5 mice in a cage.";
		END IF;
    -- If breeder cage   
	ELSE 
		IF (SELECT COUNT(*) FROM mouse AS m2 WHERE NEW.Sex = m2.Sex AND NEW.Sex = 'M' AND NEW.CageID = m2.CageID) >= 1 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Already a male breeder in this cage.";
		ELSEIF (SELECT COUNT(*) FROM mouse AS m2 WHERE NEW.Sex = m2.Sex AND NEW.Sex = 'F' AND NEW.CageID = m2.CageID) >= 1 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Already a female breeder in this cage.";
		END IF;
	END IF;
		
END //
DELIMITER ;

-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS insert_mouse_sex_restrict;

DELIMITER //

CREATE TRIGGER insert_mouse_sex_restrict
	BEFORE INSERT ON mouse
    FOR EACH ROW
BEGIN 
	IF EXISTS(SELECT CageID FROM cage AS c WHERE c.Breeding = FALSE AND c.CageID = NEW.CageID) THEN 
		IF (NEW.sex NOT IN (SELECT DISTINCT Sex FROM mouse WHERE CageID = NEW.CageID)) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot house male and female mice together in non-breeding cage.";
		END IF;
	END IF;
		
END //
DELIMITER ;

-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS update_mouse_sex_restrict;

DELIMITER //

CREATE TRIGGER update_mouse_sex_restrict
	BEFORE UPDATE ON mouse
    FOR EACH ROW
BEGIN 
	IF EXISTS(SELECT CageID FROM cage AS c WHERE c.Breeding = FALSE AND c.CageID = NEW.CageID) THEN 
		IF (NEW.sex NOT IN (SELECT DISTINCT Sex FROM mouse WHERE CageID = NEW.CageID)) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot house male and female mice together in non-breeding cage.";
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
	IF (SELECT Address FROM `user` WHERE UserID = uID) IS NULL THEN
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "No address associated with user.";
	ELSE
		SELECT CONCAT(Street, " ", City, " ", State, " ", Zip) AS "Address" FROM address WHERE AddressIndex = (SELECT Address FROM `user` WHERE UserID = uID);
	END IF;
END //

DELIMITER ;


-- --------------------------------------------------------------
DROP PROCEDURE IF EXISTS view_facility;

DELIMITER //

CREATE PROCEDURE view_facility()
BEGIN
	SELECT f.FacilityID, f.FacilityName, (SELECT COUNT(*) FROM room AS r WHERE r.FacilityID = f.FacilityID) AS roomCount
    FROM facility AS f;
END //

DELIMITER ;

-- --------------------------------------------------------------
DROP PROCEDURE IF EXISTS view_facility_access;

DELIMITER //

CREATE PROCEDURE view_facility_access
(
	IN uID INT
)
BEGIN
	IF (uID IS NULL) THEN
		SELECT uf.UserID, u.FirstName, u.LastName, uf.FacilityID, f.FacilityName 
        FROM user_facility_access AS uf 
        INNER JOIN `user` AS u
        ON uf.UserID = u.UserID
        INNER JOIN facility AS f
        ON uf.FacilityID = f.FacilityID;
    ELSE
		SELECT uf.UserID, u.FirstName, u.LastName, uf.FacilityID, f.FacilityName 
		FROM user_facility_access AS uf 
        INNER JOIN `user` AS u
		ON uf.UserID = u.UserID
		INNER JOIN facility AS f
		ON uf.FacilityID = f.FacilityID
		WHERE uf.UserID = uID;
    END IF;

END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_address;

DELIMITER //

CREATE PROCEDURE delete_address
(
	IN uID INT
)
BEGIN
	DECLARE adrID INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
            RESIGNAL;
		END;
        
	START TRANSACTION;
    
    IF (SELECT Address FROM `user` WHERE UserID = uID) IS NOT NULL THEN 
		SELECT Address INTO adrID FROM `user` WHERE UserID = uID;
	ELSE 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: User doesn't exist/doesn't have an address on file.";
    END IF;
	
    IF (SELECT COUNT(*) FROM `user` WHERE adrID = Address) > 1 THEN
		-- Multiple people at address
        UPDATE `user` SET Address = NULL WHERE UserID = uID; 
	ELSE
		UPDATE `user` SET Address = NULL WHERE UserID = uID;
		DELETE FROM address WHERE AddressIndex = adrID; 
    END IF;
    
    COMMIT;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_cage;

DELIMITER //

CREATE PROCEDURE delete_cage
(
	IN uID INT,
    IN cID INT
)
BEGIN
	IF NOT EXISTS (SELECT * FROM cage WHERE CageID = cID) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot delete cage that does not exist.";
	ELSEIF (uid) IS NULL THEN
		DELETE FROM cage WHERE CageID = cID;
	ELSE 
		IF cID IN (SELECT CageID FROM cage WHERE Manager = uID) THEN
			DELETE FROM cage WHERE CageID = cID;
		ELSE 
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot delete cage that you do not manage.";
		END IF;
    END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS initialize_rack_filled;

DELIMITER //

CREATE PROCEDURE initialize_rack_filled()
BEGIN
	UPDATE rack AS r SET FilledSlots = (SELECT COUNT(*) FROM cage AS c WHERE r.RackID = c.RackID AND c.CageStatus = 'Active');
END //

DELIMITER ;


CALL initialize_rack_filled(); -- TODO: call somewhere else ------------------------------------------------------------------------------------------------------



-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS insert_cage_trigger;

DELIMITER //

CREATE TRIGGER insert_cage_trigger
	BEFORE INSERT ON cage
    FOR EACH ROW
BEGIN 
	DECLARE temp_fill INT;
	
	IF NEW.CageStatus = 'Active' THEN 
		IF (SELECT SUM(CageSlots - FilledSlots) FROM rack AS r WHERE r.RackID = NEW.RackID) <= 0 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot put new cage onto full rack.";
		ELSE
			SELECT FilledSlots INTO temp_fill FROM rack WHERE RackID = NEW.RackID;
			UPDATE rack SET FilledSlots = (temp_fill + 1) WHERE RackID = NEW.RackID;
		END IF;
	END IF;

END //
DELIMITER ;

-- ------------------------------------------------------------------------
DROP TRIGGER IF EXISTS update_cage_trigger;

DELIMITER //

CREATE TRIGGER update_cage_trigger
	BEFORE UPDATE ON cage
    FOR EACH ROW
BEGIN 
	DECLARE temp_fill INT;

	IF NEW.CageStatus = 'Active' THEN 
		IF (SELECT SUM(CageSlots - FilledSlots) FROM rack AS r WHERE r.RackID = NEW.RackID) <= 0 THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot put new cage onto full rack.";
		ELSE
			SELECT FilledSlots INTO temp_fill FROM rack WHERE RackID = NEW.RackID;
			UPDATE rack SET FilledSlots = (temp_fill + 1) WHERE RackID = NEW.RackID;
		END IF;
	END IF;

END //
DELIMITER ;

-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS add_facility_access;

DELIMITER //

CREATE PROCEDURE add_facility_access
(
	IN uID INT,
    IN facID INT
)
BEGIN
	IF EXISTS (SELECT * FROM user_facility_access WHERE UserID = uID AND FacilityID = facID) THEN 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot create duplicate facility access records.";
	ELSE 
		INSERT INTO user_facility_access VALUE(uID, facID);
	END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_user;

DELIMITER //

CREATE PROCEDURE delete_user
(
	IN uID INT
)
BEGIN

DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
            RESIGNAL;
		END;

START TRANSACTION;

	IF EXISTS (SELECT * FROM `user` WHERE UserID = uID) THEN
		CALL delete_address(uID);
		DELETE FROM `user` WHERE UserID = uID;
	ELSE
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: User does not exist.";
    END IF;
   
COMMIT;   
    
END //

DELIMITER ;


-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_facility_access;

DELIMITER //

CREATE PROCEDURE delete_facility_access
(
	IN uID INT,
    IN facID INT
)
BEGIN
	IF EXISTS (SELECT * FROM user_facility_access WHERE UserID = uID AND FacilityID = facID) THEN
		DELETE FROM user_facility_access WHERE UserID = uID AND FacilityID = facID;
	ELSE
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: No access to remove.";
    END IF;
	
END //

DELIMITER ;

-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_facility;

DELIMITER //

CREATE PROCEDURE delete_facility
(
	IN facID INT
)
BEGIN
	IF EXISTS (SELECT * FROM facility WHERE FacilityID = facID) THEN
		DELETE FROM facility WHERE FacilityID = facID;
	ELSE
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: There is no facility associated with that ID.";
    END IF;
END //

DELIMITER ;


-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_room;

DELIMITER //

CREATE PROCEDURE delete_room
(
	IN rmID INT
)
BEGIN
	IF EXISTS (SELECT * FROM room WHERE RoomID = rmID) THEN
		DELETE FROM room WHERE RoomID = rmID;
	ELSE
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: There is no room associated with that ID.";
    END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_rack;

DELIMITER //

CREATE PROCEDURE delete_rack
(
	IN rkID INT
)
BEGIN
	IF EXISTS (SELECT * FROM rack WHERE RackID = rkID) THEN
		DELETE FROM rack WHERE RackID = rkID;
	ELSE
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: There is no rack associated with that ID.";
    END IF;
END //

DELIMITER ;


-- --------------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_genotype;

DELIMITER //

CREATE PROCEDURE delete_genotype
(
	IN geno VARCHAR(10)
)
BEGIN
	IF EXISTS (SELECT * FROM genotype WHERE GenotypeAbr = geno) THEN
		DELETE FROM genotype WHERE GenotypeAbr = geno;
	ELSE
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: There is no genotype with that abbreviation";
    END IF;
END //

DELIMITER ;


-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS delete_mouse;

DELIMITER //

CREATE PROCEDURE delete_mouse
(
	IN uID INT,
    IN eTag INT
)
BEGIN
	IF NOT EXISTS (SELECT * FROM mouse WHERE Eartag = eTag) THEN
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot delete mouse that does not exist.";
	ELSEIF (uid) IS NULL THEN
		DELETE FROM mouse WHERE Eartag = eTag;
	ELSE 
		IF eTag IN (SELECT Eartag FROM mouse AS m WHERE m.CageID IN (SELECT CageID FROM cage WHERE Manager = uID)) THEN
			DELETE FROM mouse WHERE Eartag = eTag;
		ELSE 
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot delete mouse that you do not manage.";
		END IF;
    END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS deactivate_cage;

DELIMITER //

CREATE PROCEDURE deactivate_cage
(
	IN uID INT,
    IN cID INT
)
BEGIN
DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
            RESIGNAL;
		END;
        
START TRANSACTION;

	IF NOT EXISTS (SELECT * FROM cage WHERE CageID = cID) THEN
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot modify cage that does not exist.";
	ELSEIF (uid) IS NULL THEN
		UPDATE cage SET CageStatus = 'Inactive' WHERE CageID = cID;
        UPDATE mouse SET DateOfDeath = CURRENT_DATE() WHERE CageID = cID;
	ELSE 
		IF cID IN (SELECT CageID FROM cage WHERE Manager = uID) THEN
			UPDATE cage SET CageStatus = 'Inactive' WHERE CageID = cID;
			UPDATE mouse SET DateOfDeath = CURRENT_DATE() WHERE CageID = cID;
		ELSE 
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot update cage that you do not manage.";
		END IF;
    END IF;
 
COMMIT;
    
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_cage;

DELIMITER //

CREATE PROCEDURE update_cage
(
	IN cID INT,
    IN bType VARCHAR(20),
    IN cStatus VARCHAR(8),
    IN rID INT,
    IN userID INT,
    IN manID INT
)
BEGIN
	IF NOT EXISTS (SELECT * FROM cage WHERE CageID = cID) THEN 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot update cage that doesn't exist.";
	-- ADMIN
	ELSEIF userID IS NULL THEN
		IF manID IS NULL THEN
			UPDATE cage SET BeddingType = bType, CageStatus = cStatus, RackID = rID WHERE CageID = cID;
		ELSE 
			UPDATE cage SET BeddingType = bType, CageStatus = cStatus, RackID = rID, Manager = manID WHERE CageID = cID;
		END IF;
	-- USER
    ELSE 
		IF cID IN (SELECT CageID FROM cage WHERE Manager = userID) THEN
			IF manID IS NULL THEN
				UPDATE cage SET BeddingType = bType, CageStatus = cStatus, RackID = rID WHERE CageID = cID;
			ELSE 
				UPDATE cage SET BeddingType = bType, CageStatus = cStatus, RackID = rID, Manager = manID WHERE CageID = cID;
			END IF;
		ELSE 
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot update cage that you do not manage.";
		END IF;
    END IF;	
    
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_mouse;

DELIMITER //

CREATE PROCEDURE update_mouse
(
	IN eTag INT,
    IN geno VARCHAR(10),
    IN dod VARCHAR(20),
    IN cID INT,
    IN manID INT
)
BEGIN
	IF NOT EXISTS (SELECT * FROM mouse WHERE Eartag = eTag) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot update mouse that does not exist.";
	ELSEIF manID IS NULL THEN
		UPDATE mouse SET GenotypeAbr = geno, DateOfDeath = dod, CageID = cID WHERE Eartag = eTag;
    ELSE 
		IF (SELECT CageID FROM mouse WHERE Eartag = eTag) IN (SELECT CageID FROM cage WHERE Manager = manID) THEN
			UPDATE mouse SET GenotypeAbr = geno, DateOfDeath = dod, CageID = cID WHERE Eartag = eTag;
		ELSE 
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot update mouse that you do not manage.";
		END IF;
	END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_user;

DELIMITER //

CREATE PROCEDURE update_user
(
	IN uID INT,
    IN fname VARCHAR(25),
    IN lname VARCHAR(25),
    IN mail VARCHAR(40),
    IN phone VARCHAR(11),
    IN aFlag BOOLEAN
)
BEGIN
	IF EXISTS (SELECT * FROM `user` WHERE UserID = uID) THEN 
		UPDATE `user` SET FirstName = fname, LastName = lname, Email = mail, PhoneNum = phone, AdminFlag = aFlag WHERE UserID = uID;
	ELSE 
		SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = "ERROR: Cannot update user that does not exist.";
	END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_facility;

DELIMITER //

CREATE PROCEDURE update_facility
(
	IN facID INT,
    IN facName VARCHAR(50)
)
BEGIN
	IF EXISTS (SELECT * FROM facility WHERE FacilityID = facID) THEN 
		UPDATE facility SET FacilityName = facName WHERE FacilityID = facID;
	ELSE 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot update facility that does not exist.";
	END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_room;

DELIMITER //

CREATE PROCEDURE update_room
(
	IN rID INT,
    IN cycle VARCHAR(6)
)
BEGIN
	IF EXISTS (SELECT * FROM room WHERE RoomID = rID) THEN 
		UPDATE room SET LightCycle = cycle WHERE RoomID = rID;
    ELSE 
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = "ERROR: Cannot update room that does not exist.";
	END IF;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_rack;

DELIMITER //

CREATE PROCEDURE update_rack
(
	IN rkID INT,
    IN rmID INT
)
BEGIN
	UPDATE rack SET RoomID = rmID WHERE RackID = rkID;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS update_genotype;

DELIMITER //

CREATE PROCEDURE update_genotype
(
	IN genoabr VARCHAR(10),
    IN genodesc VARCHAR(200)
)
BEGIN
	UPDATE genotype SET GenotypeDetails = genodesc WHERE GenotypeAbr = genoabr;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_room;

DELIMITER //

CREATE PROCEDURE view_room()
BEGIN
	SELECT r.RoomID, r.RoomNumber, r.LightCycle, r.FacilityID, f.FacilityName 
    FROM room AS r 
    INNER JOIN facility AS f 
    ON r.FacilityID = f.FacilityID
    ORDER BY r.RoomID, r.RoomNumber;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_rack;

DELIMITER //

CREATE PROCEDURE view_rack()
BEGIN
	SELECT RackID, CageSlots, FilledSlots, RoomID
    FROM rack
    ORDER BY RackID;
END //

DELIMITER ;


-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_cage;

DELIMITER //



CREATE PROCEDURE view_cage()
BEGIN
	SELECT c.CageStatus, c.CageID, c.Breeding, c.Manager, c.RackID, 
    (
    SELECT GROUP_CONCAT(DISTINCT GenotypeAbr SEPARATOR ', ') FROM mouse AS m WHERE c.CageID = m.CageID
	) AS Genotypes, c.BeddingType FROM cage AS c
    ORDER BY c.CageStatus, c.CageID;
END //

DELIMITER ;


-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_mouse_nofilter;

DELIMITER //



CREATE PROCEDURE view_mouse_nofilter()
BEGIN
	SELECT m.CageID, m.Eartag, m.GenotypeAbr, m.Sex, m.DOB, 
		IF (m.DateOfDeath IS NULL, TIMESTAMPDIFF(WEEK, m.DOB, CURDATE()), TIMESTAMPDIFF(DAY, m.DOB, DateOfDeath)) AS WeeksOld, 
		m.DateOfDeath, m.OriginCage, c.RackID, rm.RoomNumber, f.FacilityName,
		(SELECT CONCAT(UserID, ": ", FirstName, " ", LastName) FROM `user` WHERE USERID = c.Manager) AS Manager, c.Breeding 
	FROM mouse AS m INNER JOIN cage AS c ON c.CageID = m.CageID
	INNER JOIN rack AS r ON c.RackID = r.RackID
	INNER JOIN room AS rm ON r.RoomID = rm.RoomID
	INNER JOIN facility AS f ON rm.FacilityID = f.FacilityID
    ORDER BY m.CageID;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_mouse_byuser;

DELIMITER //



CREATE PROCEDURE view_mouse_byuser
(
	IN uID INT
)
BEGIN
	SELECT m.CageID, m.Eartag, m.GenotypeAbr, m.Sex, m.DOB, 
		IF (m.DateOfDeath IS NULL, TIMESTAMPDIFF(WEEK, m.DOB, CURDATE()), TIMESTAMPDIFF(DAY, m.DOB, DateOfDeath)) AS WeeksOld, 
		m.DateOfDeath, m.OriginCage, c.RackID, rm.RoomNumber, f.FacilityName,
		(SELECT CONCAT(UserID, ": ", FirstName, " ", LastName) FROM `user` WHERE USERID = c.Manager) AS Manager, c.Breeding 
	FROM mouse AS m INNER JOIN cage AS c ON c.CageID = m.CageID
	INNER JOIN rack AS r ON c.RackID = r.RackID
	INNER JOIN room AS rm ON r.RoomID = rm.RoomID
	INNER JOIN facility AS f ON rm.FacilityID = f.FacilityID
    WHERE c.Manager = uID;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_mouse_bygeno;

DELIMITER //



CREATE PROCEDURE view_mouse_bygeno
(
	IN geno VARCHAR(10)
)
BEGIN
	SELECT m.CageID, m.Eartag, m.GenotypeAbr, m.Sex, m.DOB, 
		IF (m.DateOfDeath IS NULL, TIMESTAMPDIFF(WEEK, m.DOB, CURDATE()), TIMESTAMPDIFF(DAY, m.DOB, DateOfDeath)) AS WeeksOld, 
		m.DateOfDeath, m.OriginCage, c.RackID, rm.RoomNumber, f.FacilityName,
		(SELECT CONCAT(UserID, ": ", FirstName, " ", LastName) FROM `user` WHERE USERID = c.Manager) AS Manager, c.Breeding 
	FROM mouse AS m INNER JOIN cage AS c ON c.CageID = m.CageID
	INNER JOIN rack AS r ON c.RackID = r.RackID
	INNER JOIN room AS rm ON r.RoomID = rm.RoomID
	INNER JOIN facility AS f ON rm.FacilityID = f.FacilityID
    WHERE m.GenotypeAbr = geno;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_mouse_byroom;

DELIMITER //



CREATE PROCEDURE view_mouse_byroom
(
	IN rmID INT
)
BEGIN
	SELECT m.CageID, m.Eartag, m.GenotypeAbr, m.Sex, m.DOB, 
		IF (m.DateOfDeath IS NULL, TIMESTAMPDIFF(WEEK, m.DOB, CURDATE()), TIMESTAMPDIFF(DAY, m.DOB, DateOfDeath)) AS WeeksOld, 
		m.DateOfDeath, m.OriginCage, c.RackID, rm.RoomNumber, f.FacilityName,
		(SELECT CONCAT(UserID, ": ", FirstName, " ", LastName) FROM `user` WHERE USERID = c.Manager) AS Manager, c.Breeding 
	FROM mouse AS m INNER JOIN cage AS c ON c.CageID = m.CageID
	INNER JOIN rack AS r ON c.RackID = r.RackID
	INNER JOIN room AS rm ON r.RoomID = rm.RoomID
	INNER JOIN facility AS f ON rm.FacilityID = f.FacilityID
    WHERE rm.RoomID = rmID;
END //

DELIMITER ;


-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_mouse_bycage;

DELIMITER //



CREATE PROCEDURE view_mouse_bycage
(
	IN cage INT
)
BEGIN
	SELECT m.CageID, m.Eartag, m.GenotypeAbr, m.Sex, m.DOB, 
		IF (m.DateOfDeath IS NULL, TIMESTAMPDIFF(WEEK, m.DOB, CURDATE()), TIMESTAMPDIFF(DAY, m.DOB, DateOfDeath)) AS WeeksOld, 
		m.DateOfDeath, m.OriginCage, c.RackID, rm.RoomNumber, f.FacilityName,
		(SELECT CONCAT(UserID, ": ", FirstName, " ", LastName) FROM `user` WHERE USERID = c.Manager) AS Manager, c.Breeding 
	FROM mouse AS m INNER JOIN cage AS c ON c.CageID = m.CageID
	INNER JOIN rack AS r ON c.RackID = r.RackID
	INNER JOIN room AS rm ON r.RoomID = rm.RoomID
	INNER JOIN facility AS f ON rm.FacilityID = f.FacilityID
    WHERE m.CageID = cage;
END //

DELIMITER ;

-- --------------------------------------------------------------

DROP PROCEDURE IF EXISTS view_mouse_byfacility;

DELIMITER //



CREATE PROCEDURE view_mouse_byfacility
(
	IN facID INT
)
BEGIN
	SELECT m.CageID, m.Eartag, m.GenotypeAbr, m.Sex, m.DOB, 
		IF (m.DateOfDeath IS NULL, TIMESTAMPDIFF(WEEK, m.DOB, CURDATE()), TIMESTAMPDIFF(DAY, m.DOB, DateOfDeath)) AS WeeksOld, 
		m.DateOfDeath, m.OriginCage, c.RackID, rm.RoomNumber, f.FacilityName,
		(SELECT CONCAT(UserID, ": ", FirstName, " ", LastName) FROM `user` WHERE USERID = c.Manager) AS Manager, c.Breeding 
	FROM mouse AS m INNER JOIN cage AS c ON c.CageID = m.CageID
	INNER JOIN rack AS r ON c.RackID = r.RackID
	INNER JOIN room AS rm ON r.RoomID = rm.RoomID
	INNER JOIN facility AS f ON rm.FacilityID = f.FacilityID
    WHERE f.FacilityID = facID;
END //

DELIMITER ;