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
    CONSTRAINT mouse_to_male_parent FOREIGN KEY (MaleParent) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT, -- Had to replace origin cage with recursive 
    CONSTRAINT mouse_to_female_parent FOREIGN KEY (FemaleParent) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT,
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
    CONSTRAINT breeding_cage_to_user FOREIGN KEY (Manager) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT male_breeder_to_mouse FOREIGN KEY (MaleID) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT female_breeder_to_mouse FOREIGN KEY (FemaleID) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE non_breeding_cage
(
	CageID INT PRIMARY KEY,
    BeddingType VARCHAR(20),
    CageStatus ENUM('Active', 'Inactive') NOT NULL,
    RackID INT NOT NULL,
    Manager INT NOT NULL,
    Mouse1 INT NOT NULL,
    Mouse2 INT,
	Mouse3 INT,
	Mouse4 INT,
	Mouse5 INT,
    CONSTRAINT nonbr_cage_to_rack FOREIGN KEY (RackID) REFERENCES rack(RackID) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT nonbr_cage_to_user FOREIGN KEY (Manager) REFERENCES `user`(UserID) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT mouse1_cage_to_mouse FOREIGN KEY (Mouse1) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT mouse2_cage_to_mouse FOREIGN KEY (Mouse2) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT mouse3_cage_to_mouse FOREIGN KEY (Mouse3) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT mouse4_cage_to_mouse FOREIGN KEY (Mouse4) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT mouse5_cage_to_mouse FOREIGN KEY (Mouse5) REFERENCES mouse(Eartag) ON UPDATE RESTRICT ON DELETE RESTRICT
);

