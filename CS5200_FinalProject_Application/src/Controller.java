import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;
import java.sql.Types;

public class Controller {

	private int appUserID;
	private String appPassword;
	private boolean adminAccess;
	private Connection conn;
	private Scanner scan;

	public Controller() {
		appUserID = -1;
		appPassword = "";
		adminAccess = false;
		conn = null;
		scan = null;
	}

	private Connection sqlLogin() {
		final String database = "jdbc:mysql://localhost:3306/mouse_housing"
								+ "?characterEncoding=UTF-8&useSSL=false";
		Connection conn = null;
		boolean connFlag = false;

		while (!connFlag) {
			// TODO uncomment
			// Use scanner to get username and password
//			System.out.println("Please enter SQL login username. Type 'q' to quit.");
//			String userName = scan.nextLine();
//
//			if (userName.toLowerCase().compareTo("q") == 0) {
//				System.exit(0);
//			}
//
//			System.out.println("Please enter SQL password.");
//			String userPassword = scan.nextLine();

			// TODO remove
			String userName = "root";
			String userPassword = "orbitmyHead93";

			// try to connect to server
			try {
				conn = DriverManager.getConnection(database, userName, userPassword);

				if (conn.isValid(5)) {
					connFlag = true;
				}

				System.out.println("SUCCESS: Connection to SQL server successful.");
			}
			catch (SQLException ex) {
				if (ex.getSQLState().compareTo("08S01") == 0) {
					System.out.println("ERROR: There is a connection issue with the server."
							+ " Check that the server is running.");
				}
				else {
					System.out.println("ERROR: Those are not viable credentials.");
				}
			}
		}
		// on success return connection
		return conn;
	}

	// helps login for established users- returns true if valid login, false if not
	private boolean appLogin_loginHelper() {
		System.out.println("Please enter app userID.");
		try {
			appUserID = Integer.parseInt(scan.nextLine());
		}
		catch (NumberFormatException e) {
			System.out.println("ERROR: Non-number input for UserID. Please try again.");
			return false;
		}
		System.out.println("Please enter app password.");
		appPassword = scan.nextLine();

		try {
			CallableStatement loginStmt = conn.prepareCall("{call login(?, ?, ?)}");
			loginStmt.registerOutParameter(1, Types.INTEGER);
			loginStmt.setInt(2, appUserID);
			loginStmt.setString(3, appPassword);

			loginStmt.execute();

			int loginResult = loginStmt.getInt(1);

			if (loginResult == 0) {
				System.out.println("ERROR: Those login credentials are incorrect.");
				return false;
			} else if (loginResult == 1) {
				adminAccess = false;
				System.out.println("SUCCESS: logged in with user access.");
				return true;

			} else if (loginResult == 2) {
				adminAccess = true;
				System.out.println("SUCCESS:- logged in with admin access.");
				return true;
			}
		} catch (SQLException ex) {
			if (ex.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: Problem logging in.");
			}
		}
		return false;
	}

	private void appLogin_adrHelper(int userID) {
		// Prompt user to add address
		String userResponse = "";
		while (userResponse.toLowerCase().compareTo("y") != 0 &&
				userResponse.toLowerCase().compareTo("n") != 0) {
			System.out.println("Would you like to add an address to the system "
					+ "for that user? (y/n)");
			userResponse = scan.nextLine();
		}

		// Create new address for user
		if (userResponse.toLowerCase().compareTo("y") == 0) {
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

				System.out.println("Address successfully added to UserID: " + userID);
			}
			catch (SQLException e) {
				if (e.getSQLState().compareTo("22001") == 0) {
					System.out.println("ERROR: Input string is too long.");
				}
				else {
					System.out.println("ERROR: An error occurred while adding the address.");
				}
			}
		}
	}

	private void appLogin_newHelper() {
		// Create new user
		try {
			CallableStatement newUserStmt = conn.prepareCall("{CALL new_user(?, ?, ?, ?, ?, ?)}");

			System.out.println("Please enter UserID.");
			int userID = Integer.parseInt(scan.nextLine());
			newUserStmt.setInt(1, userID);

			System.out.println("Please enter password. 25 characters or less.");
			String password = scan.nextLine();
			newUserStmt.setString(2, password);

			System.out.println("Please enter first name.");
			String fname = scan.nextLine();
			newUserStmt.setString(3, fname);

			System.out.println("Please enter last name.");
			String lname = scan.nextLine();
			newUserStmt.setString(4, lname);

			System.out.println("Please enter email.");
			String email = scan.nextLine();
			newUserStmt.setString(5, email);

			System.out.println("Please enter phone number.");
			String phoneNum = scan.nextLine();
			newUserStmt.setString(6, phoneNum);

			// Try and create a new user for these details
			if (newUserStmt.executeUpdate() == 1) {
				System.out.println("SUCCESS: User successfully added.");
			}

			appLogin_adrHelper(userID);
		}
		catch (SQLException ex) {
			if (ex.getErrorCode() == 1062) {
				System.out.println("ERROR: Duplicate UserID found. If you are sure this is your "
						+ "user id please contact an admin for assistance.");
			}
			else if (ex.getSQLState().compareTo("22001") == 0) {
				System.out.println("ERROR: Input string is too long.");
			}
			else {
				System.out.println("ERROR: Problem creating user account.");
			}
		}
		catch (NumberFormatException e) {
			System.out.println("ERROR: Non-number input for UserID. Please try again.");
		}
	}


	private boolean appLogin() {
		boolean flag = false;

		while (!flag) {
			System.out.println("\nWelcome to MouseHousing. Type 'login' to connect to an existing "
					+ "profile. Type 'new' to create a new user profile. Type 'q' to exit "
					+ "application.");

			String response = scan.nextLine();

			switch (response.toLowerCase()) {
				// User tries to login
				case "login":
					flag = appLogin_loginHelper();
					break;
				// User tries to create new user account in database
				case "new":
					appLogin_newHelper();
					break;
				// User quits program
				case "q":
					return false;
				default:
					System.out.println("Command not recognized.");
			}
		}
		return true;
	}

	public void programLoop() {
		scan = new Scanner(System.in);
		conn = sqlLogin();

		if (Objects.isNull(conn)) {
			System.out.println("ERROR: Connection failed to be made.");
			scan.close();
			System.exit(1);
		}

		// Check if login is successful
		if (appLogin()) {
			UserMenuInterface menu;

			if (adminAccess) {
				menu = new AdminMenu(conn);
			}
			else {
				menu = new UserMenu(conn);
			}

			menu.menuStart(appUserID);
		}

		// close connection and scanner
		try {
			conn.close();
			if (conn.isClosed()) {
				System.out.println("SUCCESS: Connection to server closed successfully.");
			}
		}
		catch (SQLException ex) {
			System.out.println("ERROR: Problem with closing connection to server.");
		}
		scan.close();
	}
}
