import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class UserMenu extends UserMenuAbstract {

	private int userID;

	public UserMenu(Connection conn) {
		this.conn = conn;
		scan = new Scanner(System.in);
	}

	@Override
	public void menuStart(int userID) {
		this.userID = userID;
		String userInput = "";

		while (userInput.toLowerCase().compareTo("q") != 0) {
			System.out.println("\nType the number of the menu option you would like. Type q to quit.");
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

	private void addMenu() {
		String userInput = "";

		while (userInput.toLowerCase().compareTo("b") != 0) {
			System.out.println("\nType the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("Add Menu:\n"
					+ "1 = add your new Address\n"
					+ "2 = add new Facility\n"
					+ "3 = add new Room\n"
					+ "4 = add new Rack\n"
					+ "5 = add new Cage\n"
					+ "6 = add new Mouse\n"
					+ "7 = add new Genotype\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					addAddress();
					break;
				case "2":
					addFacility();
					break;
				case "3":
					addRoom();
					break;
				case "4":
					addRack();
					break;
				case "5":
					addCage();
					break;
				case "6":
					addMouse();
					break;
				case "7":
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
					+ "1 = view Your Address\n"
					+ "2 = view Users\n"
					+ "3 = view Facility\n"
					+ "4 = view Your Facility Access\n"
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
					+ "1 = update Your Address\n"
					+ "2 = update Your Managed Cages\n"
					+ "3 = update Your Managed Mice\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					updateAddress();
					break;
				case "2":
					updateCage();
					break;
				case "3":
					updateMouse();
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
			System.out.println("Update Menu:\n"
					+ "1 = delete Your Address\n"
					+ "2 = delete Your Managed Cage\n"
					+ "3 = delete Your Managed Mouse\n");
			userInput = scan.nextLine();

			switch (userInput.toLowerCase()) {
				case "1":
					deleteAddress();
					break;
				case "2":
					deleteCage();
					break;
				case "3":
					deleteMouse();
					break;
				case "b":
					break;
				default:
					System.out.println("Command not recognized.");
			}
		}
	}

	@Override
	public void addAddress() {
		addAddressHelper(userID);
	}

	@Override
	public void addCage() {
		String input = "";
		while (input.toLowerCase().compareTo("y") != 0
				&& input.toLowerCase().compareTo("n") != 0) {
			System.out.println("Is the cage a breeding-cage? (y/n)");
			input = scan.nextLine();
		}

		if (input.toLowerCase().compareTo("y") == 0) {
			addCageHelper(userID, true);
		}
		else {
			addCageHelper(userID, false);
		}
	}

	@Override
	public void addMouse() {
		addMouseHelper(userID);
	}

	@Override
	public void viewAddress() {
		viewAddressHelper(userID);
	}


	@Override
	public void viewUser() {
		try {
			// Only allows user to see UserID, FirstName, LastName
			CallableStatement callableStatement =
					conn.prepareCall("{CALL user_view_user()}");
			ResultSet rs = callableStatement.executeQuery();

			while (rs.next()) {
				int uID = rs.getInt("UserID");
				String fName = rs.getString("FirstName");
				String lName = rs.getString("LastName");
				System.out.println(uID +", " + fName + " " + lName);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while adding the rack.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	@Override
	public void viewFacilityAccess() {
		try {
			// Only allows user to see UserID, FirstName, LastName
			CallableStatement callableStatement =
					conn.prepareCall("{CALL view_facility_access(?)}");
			callableStatement.setInt(1, userID);
			ResultSet rs = callableStatement.executeQuery();

			System.out.println("UserId, UserName, FacilityID: FacilityName");

			while (rs.next()) {
				int uID = rs.getInt("UserID");
				String fName = rs.getString("FirstName");
				String lName = rs.getString("LastName");
				int facilityID = rs.getInt("FacilityID");
				String facilityName = rs.getString("FacilityName");

				System.out.println(uID +", " + fName + " " + lName + ", " + facilityID
					+ ": " + facilityName);
			}
		}
		catch (SQLException e) {
			System.out.println("ERROR: An error occurred while accessing your facility access.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	@Override
	public void updateAddress() {
		updateAddressHelper(userID);
	}

	@Override
	public void updateCage() {
		try {
			String input = "";
			while (input.toLowerCase().compareTo("y") != 0
					&& input.toLowerCase().compareTo("n") != 0) {
				System.out.println("Sacrificed cage? (y/n)");
				input = scan.nextLine();
			}

			if (input.toLowerCase().compareTo("y") == 0) {
				CallableStatement callableStatement =
						conn.prepareCall("{CALL deactivate_cage(?, ?)}");
				callableStatement.setInt(1, userID);

				System.out.println("Please enter sacrificed cageID.");
				int cageID = Integer.parseInt(scan.nextLine());
				callableStatement.setInt(2, cageID);

				callableStatement.execute();

				System.out.println("SUCCESS: Cage updated to inactive and mice marked.");
			}
			else {
				CallableStatement callableStatement =
						conn.prepareCall("{CALL update_cage(?, ?, ?, ?, ?, ?)}");

				System.out.println("Please enter cageID to be updated.");
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

				callableStatement.setInt(5, userID);

				input = "";
				while (input.toLowerCase().compareTo("y") != 0
						&& input.toLowerCase().compareTo("n") != 0) {
					System.out.println("Change manager of cage? (y/n)");
					input = scan.nextLine();
				}

				// Determine if a change in manager needs to occur
				if (input.toLowerCase().compareTo("y") == 0) {
					System.out.println("Please enter new manager ID.");
					int manID = Integer.parseInt(scan.nextLine());
					callableStatement.setInt(6, manID);
				}
				else {
					callableStatement.setNull(6, Types.INTEGER);
				}

				callableStatement.execute();

				System.out.println("SUCCESS: Cage updated.");
			}
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			} else {
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

	@Override
	public void updateMouse() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL update_mouse(?, ?, ?, ?, ?)}");

			System.out.println("Please enter ear tag to be updated.");
			int eTag = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, eTag);

			System.out.println("Please enter genotype.");
			String genotype = scan.nextLine();
			callableStatement.setString(2, genotype);

			String input = "";
			while (input.toLowerCase().compareTo("y") != 0
					&& input.toLowerCase().compareTo("n") != 0) {
				System.out.println("Mouse deceased? (y/n)");
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
				callableStatement.setDate(3, sql_dod);
			}
			else {
				callableStatement.setNull(3, Types.DATE);
			}

			System.out.println("Please enter cageID where mouse is housed.");
			int cageID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(4, cageID);

			callableStatement.setInt(5, userID);

			callableStatement.execute();

			System.out.println("SUCCESS: Mouse updated.");
		}
		catch (SQLException e) {
			if (e.getSQLState().compareTo("45000") == 0) {
				System.out.println(e.getMessage());
			} else {
				System.out.println("ERROR: An error occurred while adding the cage.");
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
	public void deleteAddress() {
		deleteAddressHelper(userID);
	}

	@Override
	public void deleteCage() {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL delete_cage(?, ?)}");
			callableStatement.setInt(1, userID);

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
			callableStatement.setInt(1, userID);

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
