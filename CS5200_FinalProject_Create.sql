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

INSERT INTO address (Street, City, State, Zip) VALUE("181 Main Street", "Boston", "MA", "02111");
INSERT INTO address (Street, City, State, Zip) VALUE("47 Church Street", "Boston", "MA", "02112");

CREATE TABLE `user`
(
	UserID INT PRIMARY KEY,
    UserPassword VARCHAR(25) NOT NULL,
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    Email VARCHAR(40) NOT NULL,
    PhoneNum VARCHAR(11),
    AdminFlag BOOLEAN DEFAULT FALSE,
    Address INT,
    CONSTRAINT user_to_address_fk FOREIGN KEY (Address) REFERENCES address(AddressIndex) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO `user` VALUE (1, "password", "Billy", "Johnson", "b_johnson@domain.com", "13032021111", TRUE, 1);
INSERT INTO `user` VALUE (2, "test", "Joe", "White", "j_white@domain.com", "9491230303", FALSE, 2);
INSERT INTO `user` VALUE (3, "test", "Julie", "Rosen", "j_rosen@domain.com", "9491230341", FALSE, 2);


CREATE TABLE facility
(
	FacilityID INT PRIMARY KEY,
    FacilityName VARCHAR(50) NOT NULL
);

INSERT INTO facility VALUE (1, "Johnson Science Center");
INSERT INTO facility VALUE (23, "Medical Research Hub");
INSERT INTO facility VALUE (34, "Alvarez Building");
INSERT INTO facility VALUE (53, "Center for Biochemical Research");


CREATE TABLE user_facility_access
(
	UserID INT,
    FacilityID INT,
	CONSTRAINT user_facility_access_pk PRIMARY KEY (UserID, FacilityID),
    CONSTRAINT facility_access_to_user_fk FOREIGN KEY (UserID) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT facility_access_to_facility_fk FOREIGN KEY (FacilityID) REFERENCES facility(FacilityID) ON UPDATE RESTRICT ON DELETE CASCADE
);

INSERT INTO user_facility_access VALUE(1, 1);
INSERT INTO user_facility_access VALUE(1, 23);
INSERT INTO user_facility_access VALUE(1, 53);
INSERT INTO user_facility_access VALUE(2, 1);
INSERT INTO user_facility_access VALUE(2, 34);
INSERT INTO user_facility_access VALUE(3, 23);

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
INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (1, 102, 'dark');
INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (1, 110, 'dark');
INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (23, 23, 'light');
INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (23, 58, 'light');
INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (23, 15, 'light');
INSERT INTO room (FacilityID, RoomNumber, LightCycle) VALUE (34, 29, 'dark');


CREATE TABLE rack
(
	RackID INT PRIMARY KEY,
    CageSlots INT NOT NULL,
    FilledSlots INT,
    RoomID INT NOT NULL,
    CONSTRAINT rack_to_room_fk FOREIGN KEY (RoomID) REFERENCES room(RoomID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- Hard coded filled slots per Professor advice- removed initialize procedure
INSERT INTO rack VALUE (1, 40, 2, 1);
INSERT INTO rack VALUE (2, 10, 1, 2);
INSERT INTO rack VALUE (37, 2, 2, 7);


CREATE TABLE genotype
(
	GenotypeAbr VARCHAR(10) PRIMARY KEY, 	
    GenotypeDetails VARCHAR(200) NOT NULL UNIQUE
);

INSERT INTO genotype VALUE("B6", "C57BL/6J");
INSERT INTO genotype VALUE("Mc4r", "B6;129S4-Mc4rtm1Lowl/J");
INSERT INTO genotype VALUE("Piezo2", "B6(SJL)-Piezo2tm2.2Apat/J");


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

INSERT INTO cage VALUE(1, "aspen", 'Active', 1, 2, FALSE);
INSERT INTO cage VALUE(2, "aspen", 'Active', 1, 2, TRUE);
INSERT INTO cage VALUE(3, "Vitakraft", 'Active', 2, 1, FALSE);
INSERT INTO cage VALUE(11, "Vitakraft", 'Active', 37, 3, TRUE);
INSERT INTO cage VALUE(24, "aspen", 'Active', 37, 3, FALSE);
INSERT INTO cage VALUE(15, "aspen", 'Inactive', 37, 3, FALSE);




CREATE TABLE mouse
(
	Eartag INT PRIMARY KEY,
    GenotypeAbr VARCHAR(10) NOT NULL,
    Sex ENUM('M', 'F') NOT NULL,
    DOB DATE NOT NULL,
    DateOfDeath DATE DEFAULT NULL,
    CageID INT NOT NULL,
    OriginCage INT,
    CONSTRAINT mouse_to_genotype FOREIGN KEY (GenotypeAbr) REFERENCES genotype(GenotypeAbr) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT mouse_to_cage_fk FOREIGN KEY (CageID) REFERENCES cage(CageID) ON UPDATE RESTRICT ON DELETE RESTRICT,
	CONSTRAINT mouse_to_origincage FOREIGN KEY (CageID) REFERENCES cage(CageID) ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- Cage 1
INSERT INTO mouse VALUES (1, "B6", "m", "2020-11-11", NULL, 1, 2), (2, "B6", "m", "2020-11-11", NULL, 1, 2), 
						(3, "B6", "m", "2020-11-11", NULL, 1, 2), (4, "B6", "m", "2020-11-11", NULL, 1, 2), (9, "B6", "m", "2020-11-11", "2020-12-14", 1, 2);
                        
-- Cage 2                        
INSERT INTO mouse VALUES (14, "B6", "f", "2020-07-24", NULL, 2, NULL), (15, "B6", "m", "2020-07-25", NULL, 2, NULL);                         
                        
-- Cage 3                        
INSERT INTO mouse VALUES (5, "B6", "f", "2020-11-11", NULL, 3, 2), (6, "B6", "f", "2020-11-11", NULL, 3, 2), 
						(7, "B6", "f", "2020-11-11", NULL, 3, 2), (8, "B6", "f", "2020-11-11", NULL, 3, 2);
                        
-- Cage 11     
INSERT INTO mouse VALUES (60, "piezo2", "f", "2020-08-24", NULL, 11, NULL), (61, "piezo2", "m", "2020-08-19", NULL, 11, NULL);                        

-- Cage 24         
INSERT INTO mouse VALUES (62, "piezo2", "f", "2020-10-14", NULL, 24, 11), (63, "piezo2", "f", "2020-10-14", NULL, 24, 11), 
						(64, "piezo2", "f", "2020-10-14", NULL, 24, 11), (65, "piezo2", "f", "2020-10-14", NULL, 24, 2), (66, "piezo2", "f", "2020-10-14", NULL, 24, 11);       
                        
-- Cage 15
INSERT INTO mouse VALUES (121, "Mc4r", "f", "2019-04-14", "2020-01-10", 15, NULL), (122, "Mc4r", "f", "2019-04-19", "2020-01-10", 15, NULL);                         
