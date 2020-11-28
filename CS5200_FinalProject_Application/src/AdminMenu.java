import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class AdminMenu extends UserMenuAbstract implements AdminMenuInterface {

	public AdminMenu(Connection conn) {
		this.conn = conn;
		this.scan = new Scanner(System.in);
	}

	@Override
	public void menuStart(int userID) {
		// TODO figure out what do about userID on admin
		// this.userID = userID;


		String userInput = "";

		while (userInput.toLowerCase().compareTo("q") != 0) {
			System.out.println("\nType the number of the menu option you would like. "
					+ "Type q to quit.");
			System.out.println("Menu:\n"
					+ "1 = add data\n"
					+ "2 = view data\n"
					+ "3 = update data\n"
					+ "4 = delete data\n");

			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					addMenu();
					break;
				case "2":
					viewMenu();
					break;
				case "3":
					updateMenu();
					break;
				case "4":
					deleteMenu();
					break;
				case "q":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
		scan.close();
	}

	private void addMenu(){
		String userInput = "";

		while (userInput.toLowerCase().compareTo("b") != 0) {
			System.out.println("\nType the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("Add Menu:\n"
					+ "1 = add new Address\n"
					+ "2 = add new Facility\n"
					+ "3 = add new Facility Access\n"
					+ "4 = add new Room\n"
					+ "5 = add new Rack\n"
					+ "6 = add new Cage\n"
					+ "7 = add new Mouse\n"
					+ "8 = add new Genotype\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					addAddress();
					break;
				case "2":
					addFacility();
					break;
				case "3":
					addFacilityAccess();
					break;
				case "4":
					addRoom();
					break;
				case "5":
					addRack();
					break;
				case "6":
					addCage();
					break;
				case "7":
					addMouse();
					break;
				case "8":
					addGenotype();
					break;
				case "b":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
	}

	private void viewMenu() {
		String userInput = "";

		while (userInput.toLowerCase().compareTo("b") != 0) {
			System.out.println("\nType the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("View Menu:\n"
					+ "1 = view Address\n"
					+ "2 = view Users\n"
					+ "3 = view Facility\n"
					+ "4 = view Facility Access\n"
					+ "5 = view Room\n"
					+ "6 = view Rack\n"
					+ "7 = view Cage\n"
					+ "8 = view Mouse\n"
					+ "9 = view Genotype\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					viewAddress();
					break;
				case "2":
					viewUser();
					break;
				case "3":
					viewFacility();
					break;
				case "4":
					viewFacilityAccess();
					break;
				case "5":
					viewRoom();
					break;
				case "6":
					viewRack();
					break;
				case "7":
					viewCage();
					break;
				case "8":
					viewMouse();
					break;
				case "9":
					viewGenotype();
					break;
				case "b":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
	}

	private void updateMenu() {
		String userInput = "";

		while (userInput.toLowerCase().compareTo("b") != 0) {
			System.out.println("\nType the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("Update Menu:\n"
					+ "1 = update Address\n"
					+ "2 = update Users\n"
					+ "3 = update Facility\n"
					+ "4 = update Room\n"
					+ "5 = update Rack\n"
					+ "6 = update Cage\n"
					+ "7 = update Mouse\n"
					+ "8 = update Genotype\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					updateAddress();
					break;
				case "2":
					updateUser();
					break;
				case "3":
					updateFacility();
					break;
				case "4":
					updateRoom();
					break;
				case "5":
					updateRack();
					break;
				case "6":
					updateCage();
					break;
				case "7":
					updateMouse();
					break;
				case  "8":
					updateGenotype();
					break;
				case "b":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
	}

	private void deleteMenu() {
		String userInput = "";

		while (userInput.toLowerCase().compareTo("b") != 0) {
			System.out.println("\nType the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("Delete Menu:\n"
					+ "1 = delete Address\n"
					+ "2 = delete Users\n"
					+ "3 = delete Facility\n"
					+ "4 = delete Facility Access\n"
					+ "5 = delete Room\n"
					+ "6 = delete Rack\n"
					+ "7 = delete Cage\n"
					+ "8 = delete Mouse\n"
					+ "9 = delete Genotype\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					deleteAddress();
					break;
				case "2":
					deleteUser();
					break;
				case "3":
					deleteFacility();
					break;
				case "4":
					deleteFacilityAccess();
					break;
				case "5":
					deleteRoom();
					break;
				case "6":
					deleteRack();
					break;
				case "7":
					deleteCage();
					break;
				case "8":
					deleteMouse();
					break;
				case "9":
					deleteGenotype();
					break;
				case "b":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
	}

	@Override
	public void addFacilityAccess() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL add_facility_access(?, ?)}");
			System.out.println("Please enter UserID for new facility access.");
			int uID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, uID);

			System.out.println("Please enter facility ID.");
			int facID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(2, facID);

			if (callableStatement.executeUpdate() <= 1) {
				System.out.println("Facility access successfully granted.");
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while adding facility access.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: UserID not formatted as integer.");
		}
	}

	@Override
	public void viewFacilityAccess() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_facility_access(?)}");

			callableStatement.setNull(1, Types.INTEGER);

			ResultSet rs = callableStatement.executeQuery();

			System.out.println("UserID, Name, FacilityID, FacilityName\n"
					+ "---------------------------------------------------------------------");

			while (rs.next()) {
				int uID = rs.getInt("UserID");
				String fName = rs.getString("FirstName");
				String lName = rs.getString("LastName");
				int facilityID = rs.getInt("FacilityID");
				String facilityName = rs.getString("FacilityName");

				System.out.println(uID +", " + fName + " " + lName + ", " + facilityID + ": "
					+ facilityName);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while viewing facility access.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	// TODO
	@Override
	public void updateUser() {

	}

	// TODO
	@Override
	public void updateFacility() {

	}

	// TODO
	@Override
	public void updateRoom() {

	}

	// TODO
	@Override
	public void updateRack() {

	}

	// TODO
	@Override
	public void updateGenotype() {

	}


	@Override
	public void addAddress() {
		System.out.println("What UserID should the address be added to?");
		try {
			int input = Integer.parseInt(scan.nextLine());
			addAddressHelper(input);
		}
		catch (NumberFormatException e) {
			System.out.println("Entered UserID was not in integer format.");
		}
	}

	@Override
	public void addCage() {
		try {
			String input = "";
			while (input.toLowerCase().compareTo("y") != 0
					&& input.toLowerCase().compareTo("n") != 0) {
				System.out.println("Is the cage a breeding-cage? (y/n)");
				input = scan.nextLine();
			}

			boolean breed = input.toLowerCase().compareTo("y") == 0;

			System.out.println("What UserID is the cage managed by?");
			int manID = Integer.parseInt(scan.nextLine());
			addCageHelper(manID, breed);
		}
		catch (NumberFormatException e) {
			System.out.println("Entered UserID was not in integer format.");
		}
	}

	// TODO
	@Override
	public void addMouse() {
	}

	@Override
	public void viewAddress() {
		System.out.println("Please enter userID of address?");
		try {
			int input = Integer.parseInt(scan.nextLine());
			viewAddressHelper(input);
		}
		catch (NumberFormatException e) {
			System.out.println("Entered UserID was not in integer format.");
		}
	}

	@Override
	public void viewUser() {
		try {
			// Only allows user to see UserID, FirstName, LastName
			CallableStatement callableStatement =
					conn.prepareCall("{CALL admin_view_user()}");
			ResultSet rs = callableStatement.executeQuery();

			System.out.println("UserID, Name, Password, Email, Phone, Admin, address");
			System.out.println("------------------------------------------------------------------");

			while (rs.next()) {
				int uID = rs.getInt("UserID");
				String UserPassword = rs.getString("UserPassword");
				String fName = rs.getString("FirstName");
				String lName = rs.getString("LastName");
				String email = rs.getString("Email");
				String phoneNum = rs.getString("PhoneNum");
				boolean adminFlag = rs.getBoolean("AdminFlag");
				String address = rs.getString("Address");

				System.out.println(uID +", " + fName + " " + lName + ", " + UserPassword
						+ ", " + email + ", " + phoneNum + ", " + adminFlag + ", " + address);
			}

			System.out.println();
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while getting user data.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided values where not properly formatted as integers.");
		}
	}

	@Override
	public void updateAddress() {
		try {
			System.out.println("Please enter UserID whose address should be updated.");
			int uID = Integer.parseInt(scan.nextLine());

			updateAddressHelper(uID);
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: UserID not formatted as integer.");
		}
	}

	// TODO
	@Override
	public void updateCage() {

	}

	// TODO
	@Override
	public void updateMouse() {

	}

	@Override
	public void deleteAddress() {
		try {
			System.out.println("Please enter UserID whose address should be deleted.");
			int uID = Integer.parseInt(scan.nextLine());

			deleteAddressHelper(uID);
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: UserID not formatted as integer.");
		}
	}

	@Override
	public void deleteFacilityAccess() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_facility_access(?, ?)}");
			System.out.println("Please enter UserID for facility access removal.");
			int uID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, uID);

			System.out.println("Please enter facility ID from which access should be removed.");
			int facID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(2, facID);

			callableStatement.execute();

			System.out.println("Facility access successfully removed.");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while removing facility access.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: UserID not formatted as integer.");
		}
	}

	@Override
	public void deleteUser() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_user(?)}");
			System.out.println("Please enter UserID for deletion.");
			int uID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, uID);

			callableStatement.execute();

			System.out.println("User successfully deleted.");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while deleting user.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: User ID not formatted as integer.");
		}
	}

	@Override
	public void deleteFacility() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_facility(?)}");
			System.out.println("Please enter facilityID for deletion.");
			int facilityID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, facilityID);

			callableStatement.execute();

			System.out.println("Facility successfully deleted.");

		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while deleting facility.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: Facility ID not formatted as integer.");
		}
	}

	@Override
	public void deleteRoom() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_room(?)}");
			System.out.println("Please enter roomID for deletion.");
			int roomID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, roomID);

			callableStatement.execute();

			System.out.println("Room successfully deleted.");

		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while deleting room.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: Room ID not formatted as integer.");
		}
	}

	@Override
	public void deleteRack() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_rack(?)}");
			System.out.println("Please enter rack ID for deletion.");
			int rackID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, rackID);

			callableStatement.execute();

			System.out.println("Rack successfully deleted.");

		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while deleting rack.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException ex) {
			System.out.println("ERROR: Rack ID not formatted as integer.");
		}
	}

	@Override
	public void deleteGenotype() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_genotype(?)}");
			System.out.println("Please enter genotype abbreviation for deletion.");
			String geno = scan.nextLine();
			callableStatement.setString(1, geno);

			callableStatement.execute();

			System.out.println("Genotype successfully deleted.");

		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("ERROR: An error occurred while deleting genotype.");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}

	@Override
	public void deleteCage() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_cage(?, ?)}");
			callableStatement.setNull(1, Types.INTEGER);

			System.out.println("Please enter cageID for deletion.");
			int cageID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(2, cageID);

			callableStatement.executeUpdate();

			System.out.println("Cage " + cageID +  " successfully removed from database.");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("23000") == 0) {
				System.out.println("ERROR: Cannot delete cage with animal records attached to it.");
			}
			else if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided value was not properly formatted as an integer.");
		}
	}

	@Override
	public void deleteMouse() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_mouse(?, ?)}");
			callableStatement.setNull(1, Types.INTEGER);

			System.out.println("Please enter ear tag of mouse for deletion.");
			int eTag = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(2, eTag);


			callableStatement.executeUpdate();

			System.out.println("Mouse " + eTag +  " successfully removed from database.");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("23000") == 0) {
				System.out.println("ERROR: Cannot delete record of mouse who has offspring.");
			}
			else if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			}
			else {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		catch (NumberFormatException nx) {
			System.out.println("ERROR: Provided value was not properly formatted as an integer.");
		}
	}
}
