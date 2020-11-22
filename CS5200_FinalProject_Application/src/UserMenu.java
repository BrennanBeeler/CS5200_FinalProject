import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserMenu extends UserMenuAbstract {

	private int userID;

	public UserMenu(Connection conn) {
		this.conn = conn;
		scan = new Scanner(System.in);
	}

	// TODO maybe make abstract for this early stuff

	@Override
	public void menuStart(int userID) {
		this.userID = userID;
		String userInput = "";

		while (userInput.toLowerCase().compareTo("q") != 0) {
			System.out.println("Type the number of the menu option you would like. Type q to quit.");
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
			System.out.println("Type the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("Add Menu:\n"
					+ "1 = add new Your Address\n"
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
			System.out.println("Type the number of the menu option you would like. Type b to go "
					+ "back to the previous menu.");
			System.out.println("View Menu:\n"
					+ "1 = view Your Address\n"
					+ "2 = view Users\n"
					+ "3 = view Facility\n"
					+ "4 = view Room\n"
					+ "5 = view Rack\n"
					+ "6 = view Cage\n"
					+ "7 = view Mouse\n"
					+ "8 = view Genotype\n");
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
					viewRoom();
					break;
				case "5":
					viewRack();
					break;
				case "6":
					viewCage();
					break;
				case "7":
					viewMouse();
					break;
				case "8":
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
			System.out.println("Type the number of the menu option you would like. Type b to go "
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
			System.out.println("Type the number of the menu option you would like. Type b to go "
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

	// might not need to exist?
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
			System.out.println("An error occurred while adding the rack.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("Provided values where not properly formatted as integers.");
		}
	}

	// should work- if multiple use- then copy and change only this one
	@Override
	public void updateAddress() {
		updateAddressHelper(userID);
	}

	// might not work
	@Override
	public void updateCage() {
		updateCageHelper(userID);
	}

	// might not work
	@Override
	public void updateMouse() {
		updateMouseHelper(userID);
	}

	// should work- if multiple use- then remove from user and delete only if one user
	@Override
	public void deleteAddress() {
		deleteAddressHelper(userID);
	}

	// should work - never want to delete more than 1 cage at a time
	@Override
	public void deleteCage() {
		deleteCageHelper(userID);
	}

	// works- delete only 1 mouse at time
	@Override
	public void deleteMouse() {
		deleteMouseHelper(userID);
	}
}
