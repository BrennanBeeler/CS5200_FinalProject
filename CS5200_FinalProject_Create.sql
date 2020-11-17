DROP DATABASE IF EXISTS mouse_housing;
CREATE DATABASE mouse_housing;

use mouse_housing;

CREATE TABLE address
(
	AddressIndex INT PRIMARY KEY AUTO_INCREMENT,
    Street VARCHAR(50) NOT NULL,
    City VARCHAR(50) NOT NULL,
    State VARCHAR(2) NOT NULL,
    Zip VARCHAR(10) NOT NULL,
    CONSTRAINT unique_address UNIQUE (Street, City, State, Zip)
);

CREATE TABLE `user`
(
	UserID INT PRIMARY KEY,
    UserPassword VARCHAR(25) NOT NULL,
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    Email VARCHAR(40) NOT NULL,
    PhoneNum VARCHAR(11), -- maybe int?
    AdminFlag BOOLEAN DEFAULT FALSE,
    Address INT,
    CONSTRAINT user_to_address_fk FOREIGN KEY (Address) REFERENCES address(AddressIndex) ON UPDATE RESTRICT ON DELETE SET NULL
);

CREATE TABLE facility
(
	FacilityID INT PRIMARY KEY,
    FacilityName VARCHAR(50) NOT NULL
);

CREATE TABLE user_facility_access
(
	UserID INT,
    FacilityID INT,
	CONSTRAINT user_facility_access_pk PRIMARY KEY (UserID, FacilityID),
    CONSTRAINT facility_access_to_user_fk FOREIGN KEY (UserID) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT facility_access_to_facility_fk FOREIGN KEY (FacilityID) REFERENCES facility(FacilityID) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE room
(
	RoomID INT PRIMARY KEY,
    RoomNumber VARCHAR(10) NOT NULL,
    LightCycle ENUM('light', 'dark'),
    FacilityID INT NOT NULL,
    CONSTRAINT room_to_facility_fk FOREIGN KEY (FacilityID) REFERENCES facility(FacilityID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE rack
(
	RackID INT PRIMARY KEY,
    CageSlots INT NOT NULL,
    FilledSlots INT,
    RoomID INT NOT NULL,
    CONSTRAINT rack_to_room_fk FOREIGN KEY (RoomID) REFERENCES room(RoomID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE genotype
(
	GenotypeAbr VARCHAR(10) PRIMARY KEY, 				-- Had to add table to avoid large amount of typing with every mouse
    GenotypeDetails VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE mouse
(
	Eartag INT PRIMARY KEY,
    GenotypeAbr VARCHAR(10) NOT NULL,
    Sex ENUM('M', 'F') NOT NULL,
    DOB DATE NOT NULL,
    DateOfDeath DATE DEFAULT NULL,
    NumLitters INT DEFAULT 0,
    MaleParent INT NOT NULL,
    FemaleParent INT NOT NULL,
    CONSTRAINT mouse_to_genotype FOREIGN KEY (GenotypeAbr) REFERENCES genotype(GenotypeAbr) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE breeding_cage
(
	CageID INT PRIMARY KEY,
    BeddingType VARCHAR(20),
    CageStatus ENUM('Active', 'Inactive') NOT NULL,
    RackID INT NOT NULL,
    Manager INT NOT NULL,
    MaleID INT NOT NULL,
    FemaleID INT NOT NULL,
    CONSTRAINT breeding_cage_to_rack FOREIGN KEY (RackID) REFERENCES rack(RackID) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT breeding_cage_to_user FOREIGN KEY (Manager) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE non_breeding_cage
(
	CageID INT PRIMARY KEY,
    BeddingType VARCHAR(20),
    CageStatus ENUM('Active', 'Inactive') NOT NULL,
    RackID INT NOT NULL,
    Manager INT NOT NULL,
    CONSTRAINT nonbr_cage_to_rack FOREIGN KEY (RackID) REFERENCES rack(RackID) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT nonbr_cage_to_user FOREIGN KEY (Manager) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE origin_cage
(
	Eartag INT,
    CageID INT,
    CONSTRAINT origin_cage_pk PRIMARY KEY (Eartag, CageID),
    CONSTRAINT origin_cage_to_mouse_fk FOREIGN KEY (Eartag) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT origin_cage_to_breeding_cage_fk FOREIGN KEY (CageID) REFERENCES breeding_cage(CageID) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE cages_to_mice
(
	Eartag INT,
    CageID INT,
    CONSTRAINT cages_to_mice_pk PRIMARY KEY (Eartag, CageID),
    CONSTRAINT cages_to_mice_to_mouse_fk FOREIGN KEY (Eartag) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT cages_to_mice_to_breeding_cage_fk FOREIGN KEY (CageID) REFERENCES breeding_cage(CageID) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT cages_to_mice_to_nonbr_cage_fk FOREIGN KEY (CageID) REFERENCES non_breeding_cage(CageID) ON UPDATE RESTRICT ON DELETE CASCADE
);


use mouse_housing;
DROP FUNCTION IF EXISTS login;

DELIMITER //
CREATE FUNCTION login(
	uName VARCHAR(255),
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
	ELSEIF (admincounter = 1) THEN 
		RETURN 2;
	ELSE
		RETURN -1;
	END IF;
END//

DELIMITER ;









