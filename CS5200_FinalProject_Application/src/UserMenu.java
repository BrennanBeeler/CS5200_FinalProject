import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class UserMenu implements UserMenuInterface {
	private Connection conn;
	private Scanner scan;
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
					+ "2 = view User\n"
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

			if (newAddressStmt.executeUpdate() == 1) {
				System.out.println("Address successfully added to UserID: " + userID + "\n");
			}
		}
		catch (SQLException e) {
			System.out.println("An error occurred while adding the address.");
		}
	}

	@Override
	public void addFacility() {
		try {
			CallableStatement newAddressStmt =
					conn.prepareCall("{CALL new_facility(?, ?)}");

			System.out.println("Please facility ID.");
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
			System.out.println("An error occurred while adding the facility.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("Provided facilityID was not an integer.");
		}
	}

	@Override
	public void addRoom() {

	}

	@Override
	public void addRack() {

	}

	@Override
	public void addCage() {

	}

	@Override
	public void addMouse() {

	}

	@Override
	public void addGenotype() {

	}

	@Override
	public void viewAddress() {

	}

	@Override
	public void viewUser() {

	}

	@Override
	public void viewFacility() {

	}

	@Override
	public void viewRoom() {

	}

	@Override
	public void viewRack() {

	}

	@Override
	public void viewCage() {

	}

	@Override
	public void viewMouse() {

	}

	@Override
	public void viewGenotype() {

	}

	@Override
	public void updateAddress() {

	}

	@Override
	public void updateCage() {

	}

	@Override
	public void updateMouse() {

	}

	@Override
	public void deleteAddress() {

	}

	@Override
	public void deleteCage() {

	}

	@Override
	public void deleteMouse() {

	}
}
