import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;
import java.sql.Types;

//TODO Maybe rename to not be controller since not using MCV architecture
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
		Connection conn = null;
		Scanner scan = null;
	}

	// TODO move into view?
	private Connection sqlLogin() {
		final String database = "jdbc:mysql://localhost:3306/mouse_housing?characterEncoding=UTF-8&useSSL=false";
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

				System.out.println("Connection to SQL server successful.");
			}
			catch (SQLException ex) {
				if (ex.getSQLState().compareTo("08S01") == 0) {
					System.out.println("There is a connection issue with the server."
							+ " Check that the server is running.");
				}
				else {
					System.out.println("Those are not viable credentials.");
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
			System.out.println("Non-number input for UserID. Please try again.");
			return false;
		}
		System.out.println("Please enter app password.");
		appPassword = scan.nextLine();

		try {
			CallableStatement loginStmt = conn.prepareCall("{? = call login(?, ?)}");
			loginStmt.registerOutParameter(1, Types.INTEGER);
			loginStmt.setInt(2, appUserID);
			loginStmt.setString(3, appPassword);

			loginStmt.execute();

			int loginResult = loginStmt.getInt(1);

			if (loginResult == 0) {
				System.out.println("Those login credentials are incorrect.");
				return false;
			} else if (loginResult == 1) {
				adminAccess = false;
				System.out.println("Success- logged in as a user.");
				return true;

			} else if (loginResult == 2) {
				adminAccess = true;
				System.out.println("Success- logged in as a admin.");
				return true;
			}
		} catch (SQLException ex) {
			// TODO figure out what to do here
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}


		return false;
	}

	private void appLogin_adrHelper(int userID) {
		// Prompt user to add address
		String userResponse = "";
		while (userResponse.toLowerCase().compareTo("y") != 0 &&
				userResponse.toLowerCase().compareTo("n") != 0) {
			System.out.println("Would you like to add an address to the system "
					+ "for that user?");
			userResponse = scan.nextLine();
		}

		if (userResponse.toLowerCase().compareTo("y") == 0) {
			boolean adrFlag = false;
			while (!adrFlag) {
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

					if (newAddressStmt.executeUpdate() >= 1) {
						adrFlag = true;
						System.out.println("Address successfully added to UserID: " + userID);
					}
				}
				catch (SQLException e) {
					System.out.println("An error occurred while adding the address.");
					return;
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
				System.out.println("User successfully added.");
			}

			appLogin_adrHelper(userID);
		}
		catch (SQLException ex) {
			if (ex.getErrorCode() == 1062) {
				System.out.println("Duplicate UserID found. If you are sure this is your "
						+ "user id please contact an admin for assistance.");
			}
			else {
				// TODO: remove?
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		catch (NumberFormatException e) {
			System.out.println("Non-number input for UserID. Please try again.");
		}
	}


	private void appLogin() {
		boolean flag = false;

		while (!flag) {
			System.out.println("Welcome to MouseHousing. Type 'login' to connect to an existing "
					+ "profile. Type 'new' to create a new user profile. Type 'q' to exit "
					+ "application.");

			String response = scan.nextLine();

			// User tries to login
			if (response.toLowerCase().compareTo("login") == 0) {
				flag = appLogin_loginHelper();
			}
			// User tries to create new user account in database
			else if (response.toLowerCase().compareTo("new") == 0) {
				appLogin_newHelper();
			}
			// User quits program
			else if (response.toLowerCase().compareTo("q") == 0) {
				scan.close();
				System.exit(0);
			}
		}
	}

	public void programLoop() {
		scan = new Scanner(System.in);
		conn = sqlLogin();


		if (Objects.isNull(conn)) {
			System.out.println("Connection failed to be made.");
			scan.close();
			System.exit(1);
		}

		appLogin();

		UserMenuInterface menu;

		if (adminAccess) {
			menu = new AdminMenu(conn);
		}
		else {
			menu = new UserMenu(conn);
		}

		menu.menuStart(appUserID);

		// close connection and scanner
		try {
			conn.close();
			if (conn.isClosed()) {
				System.out.println("Connection to server closed successfully.");
			}
		}
		catch (SQLException ex) {
			// TODO figure out if actually want these
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		scan.close();
	}
}
