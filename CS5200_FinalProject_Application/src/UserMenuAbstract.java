import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public abstract class UserMenuAbstract implements UserMenuInterface{
	protected Connection conn;
	protected Scanner scan;

	protected void addAddressHelper(int userID) {
		try {
			CallableStatement newAddressStmt =
					conn.prepareCall("{CALL new_address(?, ?, ?, ?, ?)}");
			newAddressStmt.setInt(1, userID);

			System.out.println("Please enter street address.");
			String strAddress = scan.nextLine();
			newAddressStmt.setString(2, strAddress);

			System.out.println("Please enter city.");
			String city = scan.nextLine();
			newAddressStmt.setString(3, city);

			System.out.println("Please enter state abbreviation.");
			String state = scan.nextLine();
			newAddressStmt.setString(4, state);

			System.out.println("Please enter zip code.");
			String zip = scan.nextLine();
			newAddressStmt.setString(5, zip);

			newAddressStmt.executeUpdate();
			System.out.println("Address successfully added to UserID: " + userID + "\n");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while adding the address.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}

	@Override
	public void addFacility() {
		try {
			CallableStatement newAddressStmt =
					conn.prepareCall("{CALL new_facility(?, ?)}");

			System.out.println("Please enter facility ID.");
			int fID = Integer.parseInt(scan.nextLine());
			newAddressStmt.setInt(1, fID);

			System.out.println("Please enter facility name.");
			String fname = scan.nextLine();
			newAddressStmt.setString(2, fname);

			if (newAddressStmt.executeUpdate() == 1) {
				System.out.println("Facility successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while adding the facility.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided facilityID was not an integer.");
		}
	}

	@Override
	public void addRoom() {
		try {
			// TODO show facilites ?

			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_room(?, ?, ?)}");

			System.out.println("Please enter facility ID of room.");
			int fID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, fID);

			System.out.println("Please enter room number.");
			String roomNum = scan.nextLine();
			callableStatement.setString(2, roomNum);

			System.out.println("Please enter light cycle. (light/dark)");
			String lightCycle = scan.nextLine();
			callableStatement.setString(3, lightCycle);

			if (lightCycle.toLowerCase().compareTo("light") != 0
					&& lightCycle.toLowerCase().compareTo("dark") != 0) {
				System.out.println("Invalid light cycle input.");
				return;
			}

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Room successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while adding the room.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided roomID/facilityID was not an integer.");
		}
	}

	@Override
	public void addRack() {
		try {
			// TODO show rooms ?

			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_rack(?, ?, ?)}");

			System.out.println("Please enter rackID.");
			int rackID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, rackID);

			System.out.println("Please enter number of cage slots.");
			int slots = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(2, slots);

			System.out.println("Please enter roomID where rack is stored.");
			int roomID = Integer.parseInt(scan.nextLine());

			callableStatement.setInt(3, roomID);

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Rack successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while adding the rack.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided values where not properly formatted as integers.");
		}
	}

	protected void addCageHelper(int uID, boolean breed) {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_cage(?, ?, ?, ?, ?, ?)}");

			System.out.println("Please enter cageID for new cage.");
			int cageID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, cageID);

			System.out.println("Please enter bedding type.");
			String bedding = scan.nextLine();
			callableStatement.setString(2, bedding);

			System.out.println("Please enter cageStatus.(active/inactive)");
			String actCage = scan.nextLine();
			callableStatement.setString(3, actCage);

			System.out.println("Please enter rackID where cage is housed.");
			int rackID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(4, rackID);

			callableStatement.setInt(5, uID);

			callableStatement.setBoolean(6, breed);

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Cage successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while adding the cage.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided values where not properly formatted as integers.");
		}
	}

	protected void addMouseHelper(int userID) {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_mouse(?, ?, ?, ?, ?, ?, ?, ?)}");

			System.out.println("Please enter eartag.");
			int earTag = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, earTag);

			System.out.println("Please enter genotype abbreviation.");
			String geno = scan.nextLine();
			callableStatement.setString(2, geno);

			System.out.println("Please enter sex.(m/f)");
			String sex = scan.nextLine();
			callableStatement.setString(3, sex);

			System.out.println("Please enter date of birth.(mm-dd-yyyy)");
			String dob = scan.nextLine();
			// get date from string and store in SimpleDateFormat
			SimpleDateFormat smp_dob = new SimpleDateFormat("MM-dd-yyyy");
			// translate from smp_date to util to sql
			java.util.Date util_dob = smp_dob.parse(dob);
			java.sql.Date sql_dob = new java.sql.Date(util_dob.getTime());
			callableStatement.setDate(4, sql_dob);

			String input = "";
			while (input.toLowerCase().compareTo("y") != 0
					&& input.toLowerCase().compareTo("n") != 0) {
				System.out.println("Is the mouse dead? (y/n)");
				input = scan.nextLine();
			}

			if (input.toLowerCase().compareTo("y") == 0) {
				System.out.println("Please enter date of death.(mm-dd-yyyy)");
				String dod = scan.nextLine();
				// get date from string and store in SimpleDateFormat
				SimpleDateFormat smp_dod = new SimpleDateFormat("MM-dd-yyyy");
				// translate from smp_date to util to sql
				java.util.Date util_dod = smp_dod.parse(dod);
				java.sql.Date sql_dod = new java.sql.Date(util_dod.getTime());
				callableStatement.setDate(5, sql_dod);
			}
			else {
				callableStatement.setNull(5, Types.DATE);
			}

			System.out.println("Please enter cageID where mouse is housed.");
			int cageID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(6, cageID);

			// Allows mice from external sources to be entered as null - should be only way
			input = "";
			while (input.toLowerCase().compareTo("y") != 0
					&& input.toLowerCase().compareTo("n") != 0) {
				System.out.println("Mouse from external source? (y/n)");
				input = scan.nextLine();
			}

			if (input.toLowerCase().compareTo("y") == 0) {
				callableStatement.setNull(7, Types.INTEGER);
			}
			else {
				System.out.println("Please enter origin cage ID.");
				int origin = Integer.parseInt(scan.nextLine());
				callableStatement.setInt(7, origin);
			}

			// TO be used to confirm that the mouse is added to a cage managed by specified user
			callableStatement.setInt(8, userID);

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Mouse successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while adding the mouse.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided values where not properly formatted as integers.");
		}
		catch (ParseException e) {
			System.out.println("ERROR: Incorrect date formatting.");
		}
	}

	@Override
	public void addGenotype() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_genotype(?, ?)}");

			System.out.println("Please enter genotype abbreviation.");
			String genoabr = scan.nextLine();
			callableStatement.setString(1, genoabr);

			System.out.println("Please enter full genotype.");
			String genofull = scan.nextLine();
			callableStatement.setString(2, genofull);

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Genotype successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while adding the genotype.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}

	protected void viewAddressHelper(int userID) {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_address(?)}");
			callableStatement.setInt(1, userID);

			ResultSet rs = callableStatement.executeQuery();

			while (rs.next()) {
				String address = rs.getString("Address");
				System.out.println("UserID " + userID + " address: " + address);
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while viewing the address.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}

	@Override
	public void viewFacility() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_facility()}");

			ResultSet rs = callableStatement.executeQuery();

			while (rs.next()) {
				int fID = rs.getInt("FacilityID");
				String facilityName = rs.getString("FacilityName");
				int roomCount = rs.getInt("roomCount");
				System.out.println(fID + ": " + facilityName + ", # rooms: " + roomCount);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing facilities.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	@Override
	public void viewRoom() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_room()}");

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("RoomID, RoomNumber, Light Cycle, FacilityID: FacilityName");

			while (rs.next()) {
				int rmID = rs.getInt("RoomID");
				String roomNumber = rs.getString("RoomNumber");
				String lightCycle = rs.getString("LightCycle");
				int facilityID = rs.getInt("FacilityID");
				String facilityName = rs.getString("FacilityName");

				System.out.println(rmID + ", " + roomNumber + ", " + lightCycle
						+ ", " + facilityID + ": " + facilityName);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing rooms.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	@Override
	public void viewRack() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_rack()}");

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("RackID, Filled/TotalSlots, RoomID");

			while (rs.next()) {
				int rackID = rs.getInt("RackID");
				int totSlots = rs.getInt("CageSlots");
				int fSlots = rs.getInt("FilledSlots");
				int rmID = rs.getInt("RoomID");

				System.out.println(rackID + ", " + fSlots + "/" + totSlots + ", " + rmID);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing racks.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	@Override
	public void viewCage() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_cage()}");

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageStatus, CageID, Breeding, ManagerID, RackID, "
					+ "{Genotypes}, Bedding");

			while (rs.next()) {
				String cageStatus = rs.getString("CageStatus");
				int cageID = rs.getInt("CageID");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				int manager = rs.getInt("Manager");
				int rackID = rs.getInt("RackID");
				String genotypes = rs.getString("Genotypes");
				String bedding = rs.getString("BeddingType");

				System.out.println(cageStatus + ", " + cageID + ", " + breeding + ", " + manager
						+ ", " + rackID + ", {" + genotypes + "}, " + bedding);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing cages.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	private void viewMouse_Menu() {
		String userInput = "";

		while (userInput.toLowerCase().compareTo("b") != 0) {
			System.out.println("\nHow would you like to filter mouse results? Type b to "
					+ "go back to the previous menu.");
			System.out.println("1 = No Filter\n"
					+ "2 = By User\n"
					+ "3 = By Genotype\n"
					+ "4 = By Room\n"
					+ "5 = By Cage\n"
					+ "6 = By Facility\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					viewMouse_NoFilter();
					break;
				case "2":
					viewMouse_ByUser();
					break;
				case "3":
					viewMouse_ByGenotype();
					break;
				case "4":
					viewMouseByRoom();
					break;
				case "5":
					viewMouseByCage();
					break;
				case "6":
					viewMouseByFacility();
					break;
				case "b":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
	}

	private void viewMouse_NoFilter() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_mouse_nofilter()}");

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageID, Eartag, GenotypeAbr, Sex, DOB, "
					+ "WeeksOld, DateOfDeath, OriginCage, RackID, RoomNumber, FacilityName, "
					+ "Manager, Breeding");

			while (rs.next()) {
				int cageID = rs.getInt("CageID");
				int eartag = rs.getInt("Eartag");
				String genotypeAbr = rs.getString("GenotypeAbr");
				String sex = rs.getString("Sex");
				Date dob = rs.getDate("DOB");
				int weeksOld = rs.getInt("WeeksOld");
				Date dateOfDeath = rs.getDate("DateOfDeath");
				int originCage = rs.getInt("OriginCage");
				int rackID = rs.getInt("RackID");
				String roomNumber = rs.getString("RoomNumber");
				String facilityName = rs.getString("FacilityName");
				String manager = rs.getString("Manager");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				System.out.println(cageID + ", " + eartag + ", " + genotypeAbr +  ", " + sex + ", "
						+ dob + ", " + weeksOld + ", " + dateOfDeath + ", " + originCage
						+ ", " + rackID + ", " + roomNumber + ", " + facilityName +	", "
						+ manager + ", " + breeding);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing mice.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	private void viewMouse_ByUser() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_mouse_byuser(?)}");

			System.out.println("Please enter userID.");
			int uID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, uID);

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageID, Eartag, GenotypeAbr, Sex, DOB, "
					+ "WeeksOld, DateOfDeath, OriginCage, RackID, RoomNumber, FacilityName"
					+ "Manager, Breeding");

			while (rs.next()) {
				int cageID = rs.getInt("CageID");
				int eartag = rs.getInt("Eartag");
				String genotypeAbr = rs.getString("GenotypeAbr");
				String sex = rs.getString("Sex");
				Date dob = rs.getDate("DOB");
				int weeksOld = rs.getInt("WeeksOld");
				Date dateOfDeath = rs.getDate("DateOfDeath");
				int originCage = rs.getInt("OriginCage");
				int rackID = rs.getInt("RackID");
				String roomNumber = rs.getString("RoomNumber");
				String facilityName = rs.getString("FacilityName");
				String manager = rs.getString("Manager");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				System.out.println(cageID + ", " + eartag + ", " + genotypeAbr +  ", " + sex + ", "
						+ dob + ", " + weeksOld + ", " + dateOfDeath + ", " + originCage
						+ ", " + rackID + ", " + roomNumber + ", " + facilityName +	", "
						+ manager + ", " + breeding);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing mice.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: UserID not formatted as integer.");
		}
	}

	private void viewMouse_ByGenotype() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_mouse_bygeno(?)}");

			System.out.println("Please enter genotype abbreviation.");
			String geno = scan.nextLine();
			callableStatement.setString(1, geno);

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageID, Eartag, GenotypeAbr, Sex, DOB, "
					+ "WeeksOld, DateOfDeath, OriginCage, RackID, RoomNumber, FacilityName"
					+ "Manager, Breeding");

			while (rs.next()) {
				int cageID = rs.getInt("CageID");
				int eartag = rs.getInt("Eartag");
				String genotypeAbr = rs.getString("GenotypeAbr");
				String sex = rs.getString("Sex");
				Date dob = rs.getDate("DOB");
				int weeksOld = rs.getInt("WeeksOld");
				Date dateOfDeath = rs.getDate("DateOfDeath");
				int originCage = rs.getInt("OriginCage");
				int rackID = rs.getInt("RackID");
				String roomNumber = rs.getString("RoomNumber");
				String facilityName = rs.getString("FacilityName");
				String manager = rs.getString("Manager");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				System.out.println(cageID + ", " + eartag + ", " + genotypeAbr +  ", " + sex + ", "
						+ dob + ", " + weeksOld + ", " + dateOfDeath + ", " + originCage
						+ ", " + rackID + ", " + roomNumber + ", " + facilityName +	", "
						+ manager + ", " + breeding);
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while viewing mice.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}

	private void viewMouseByRoom() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_mouse_byroom(?)}");

			System.out.println("Please enter room ID.");
			int rm = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, rm);

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageID, Eartag, GenotypeAbr, Sex, DOB, "
					+ "WeeksOld, DateOfDeath, OriginCage, RackID, RoomNumber, FacilityName"
					+ "Manager, Breeding");

			while (rs.next()) {
				int cageID = rs.getInt("CageID");
				int eartag = rs.getInt("Eartag");
				String genotypeAbr = rs.getString("GenotypeAbr");
				String sex = rs.getString("Sex");
				Date dob = rs.getDate("DOB");
				int weeksOld = rs.getInt("WeeksOld");
				Date dateOfDeath = rs.getDate("DateOfDeath");
				int originCage = rs.getInt("OriginCage");
				int rackID = rs.getInt("RackID");
				String roomNumber = rs.getString("RoomNumber");
				String facilityName = rs.getString("FacilityName");
				String manager = rs.getString("Manager");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				System.out.println(cageID + ", " + eartag + ", " + genotypeAbr +  ", " + sex + ", "
						+ dob + ", " + weeksOld + ", " + dateOfDeath + ", " + originCage
						+ ", " + rackID + ", " + roomNumber + ", " + facilityName +	", "
						+ manager + ", " + breeding);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing mice.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: RoomID not formatted as integer.");
		}
	}

	private void viewMouseByCage() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_mouse_bycage(?)}");

			System.out.println("Please enter Cage ID.");
			int cage = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, cage);

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageID, Eartag, GenotypeAbr, Sex, DOB, "
					+ "WeeksOld, DateOfDeath, OriginCage, RackID, RoomNumber, FacilityName"
					+ "Manager, Breeding");

			while (rs.next()) {
				int cageID = rs.getInt("CageID");
				int eartag = rs.getInt("Eartag");
				String genotypeAbr = rs.getString("GenotypeAbr");
				String sex = rs.getString("Sex");
				Date dob = rs.getDate("DOB");
				int weeksOld = rs.getInt("WeeksOld");
				Date dateOfDeath = rs.getDate("DateOfDeath");
				int originCage = rs.getInt("OriginCage");
				int rackID = rs.getInt("RackID");
				String roomNumber = rs.getString("RoomNumber");
				String facilityName = rs.getString("FacilityName");
				String manager = rs.getString("Manager");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				System.out.println(cageID + ", " + eartag + ", " + genotypeAbr +  ", " + sex + ", "
						+ dob + ", " + weeksOld + ", " + dateOfDeath + ", " + originCage
						+ ", " + rackID + ", " + roomNumber + ", " + facilityName +	", "
						+ manager + ", " + breeding);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing mice.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: Cage ID not formatted as integer.");
		}
	}

	private void viewMouseByFacility() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_mouse_byfacility(?)}");

			System.out.println("Please enter Facility ID.");
			int facID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, facID);

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("CageID, Eartag, GenotypeAbr, Sex, DOB, "
					+ "WeeksOld, DateOfDeath, OriginCage, RackID, RoomNumber, FacilityName"
					+ "Manager, Breeding");

			while (rs.next()) {
				int cageID = rs.getInt("CageID");
				int eartag = rs.getInt("Eartag");
				String genotypeAbr = rs.getString("GenotypeAbr");
				String sex = rs.getString("Sex");
				Date dob = rs.getDate("DOB");
				int weeksOld = rs.getInt("WeeksOld");
				Date dateOfDeath = rs.getDate("DateOfDeath");
				int originCage = rs.getInt("OriginCage");
				int rackID = rs.getInt("RackID");
				String roomNumber = rs.getString("RoomNumber");
				String facilityName = rs.getString("FacilityName");
				String manager = rs.getString("Manager");
				String breeding = rs.getString("Breeding");

				if (breeding.compareTo("0") == 0) {
					breeding = "non-breeding";
				}
				else {
					breeding = "breeding";
				}

				System.out.println(cageID + ", " + eartag + ", " + genotypeAbr +  ", " + sex + ", "
						+ dob + ", " + weeksOld + ", " + dateOfDeath + ", " + originCage
						+ ", " + rackID + ", " + roomNumber + ", " + facilityName +	", "
						+ manager + ", " + breeding);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing mice.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: Facility ID not formatted as integer.");
		}
	}

	@Override
	public void viewMouse() {
		viewMouse_Menu();
	}

	@Override
	public void viewGenotype() {
		try {
			// Only allows user to see UserID, FirstName, LastName
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_genotype()}");
			ResultSet rs = callableStatement.executeQuery();

			while (rs.next()) {
				String genoabr = rs.getString("GenotypeAbr");
				String genofull = rs.getString("GenotypeDetails");
				System.out.println(genoabr + ": " + genofull);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while accessing genotype data.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	protected void updateAddressHelper(int userID) {
		try {
			// Only allows user to see UserID, FirstName, LastName
			CallableStatement callableStatement =
					conn.prepareCall("{CALL update_address(?, ?, ?, ?, ?)}");
			callableStatement.setInt(1, userID);

			System.out.println("Please enter street address.");
			String strAddress = scan.nextLine();
			callableStatement.setString(2, strAddress);

			System.out.println("Please enter city.");
			String city = scan.nextLine();
			callableStatement.setString(3, city);

			System.out.println("Please enter state abbreviation.");
			String state = scan.nextLine();
			callableStatement.setString(4, state);

			System.out.println("Please enter zip code.");
			String zip = scan.nextLine();
			callableStatement.setString(5, zip);

			callableStatement.execute();

			System.out.println("Address successfully updated for UserID: " + userID + "\n");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: An error occurred while updating address.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}

	// Needs to delete address if user is only person there, else remove the address from that user
	protected void deleteAddressHelper(int userID) {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_address(?)}");
			callableStatement.setInt(1, userID);

			callableStatement.execute();

			System.out.println("\nAddress successfully deleted for UserID: " + userID + "\n");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while deleting address.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}
}
