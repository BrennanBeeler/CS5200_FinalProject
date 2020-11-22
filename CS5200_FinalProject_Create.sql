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
    CONSTRAINT user_to_address_fk FOREIGN KEY (Address) REFERENCES address(AddressIndex) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO `user` VALUE (1, "password", "Billy", "Johnson", "b_johnson@domain.com", "13032021111", TRUE, NULL);
INSERT INTO `user` VALUE (2, "test", "Joe", "White", "j_white@domain.com", "9491230303", FALSE, NULL);


CREATE TABLE facility
(
	FacilityID INT PRIMARY KEY,
    FacilityName VARCHAR(50) NOT NULL
);

INSERT INTO facility VALUE (1, "Johnson Science Center");

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
	RoomID INT PRIMARY KEY AUTO_INCREMENT,
	FacilityID INT NOT NULL,
    RoomNumber VARCHAR(10) NOT NULL,
    LightCycle ENUM('light', 'dark'),
    CONSTRAINT UNIQUE(RoomNumber, FacilityID),
    CONSTRAINT room_to_facility_fk FOREIGN KEY (FacilityID) REFERENCES facility(FacilityID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (1, 1, 'light');

CREATE TABLE rack
(
	RackID INT PRIMARY KEY,
    CageSlots INT NOT NULL,
    FilledSlots INT,
    RoomID INT NOT NULL,
    CONSTRAINT rack_to_room_fk FOREIGN KEY (RoomID) REFERENCES room(RoomID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO rack VALUE (1, 100, 0, 1);

CREATE TABLE genotype
(
	GenotypeAbr VARCHAR(10) PRIMARY KEY, 				-- Had to add table to avoid large amount of typing with every mouse
    GenotypeDetails VARCHAR(200) NOT NULL UNIQUE
);

INSERT INTO genotype VALUE("test", "testing info");

CREATE TABLE cage
(
	CageID INT PRIMARY KEY,
    BeddingType VARCHAR(20),
    CageStatus ENUM('Active', 'Inactive') NOT NULL,
    RackID INT NOT NULL,
    Manager INT NOT NULL,
    Breeding BOOLEAN NOT NULL,
    CONSTRAINT cage_to_rack FOREIGN KEY (RackID) REFERENCES rack(RackID) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT cage_to_user FOREIGN KEY (Manager) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO cage VALUE(1, "test", 'Active', 1, 2, FALSE);
INSERT INTO cage VALUE(2, "test", 'Active', 1, 2, TRUE);


CREATE TABLE mouse
(
	Eartag INT PRIMARY KEY,
    GenotypeAbr VARCHAR(10) NOT NULL,
    Sex ENUM('M', 'F') NOT NULL,
    DOB DATE NOT NULL,
    DateOfDeath DATE DEFAULT NULL,
    CageID INT NOT NULL,
    OriginCage INT,
    CONSTRAINT mouse_to_genotype FOREIGN KEY (GenotypeAbr) REFERENCES genotype(GenotypeAbr) ON UPDATE RESTRICT ON DELETE RESTRICT,
	CONSTRAINT mouse_to_cage_fk FOREIGN KEY (CageID) REFERENCES cage(CageID) ON UPDATE RESTRICT ON DELETE RESTRICT,
	CONSTRAINT mouse_to_origincage FOREIGN KEY (CageID) REFERENCES cage(CageID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO mouse VALUES (1, "test", "m", "2000-11-11", NULL, 1, 2), (2, "test", "m", "2000-11-11", NULL, 1, 2), 
						(3, "test", "m", "2000-11-11", NULL, 1, 2), (4, "test", "m", "2000-11-11", NULL, 1, 2);
