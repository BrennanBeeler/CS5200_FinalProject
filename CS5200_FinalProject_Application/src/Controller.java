import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;
import java.sql.Types;

public class Controller {

	private String appUserID;
	private String appPassword;

	public Controller() {
		appUserID = "";
		appPassword = "";
	}


	// TODO move into view?
	private Connection sqlLogin() {
		final String database = "jdbc:mysql://localhost:3306/mouse_housing?characterEncoding=UTF-8&useSSL=false";
		Connection conn = null;
		boolean connFlag = false;
		Scanner in = new Scanner(System.in);

		while (!connFlag) {
			// Use scanner to get username and password
			System.out.println("Please enter SQL login username. Type 'q' to quit.");
			String userName = in.nextLine();

			if (userName.toLowerCase().compareTo("q") == 0) {
				System.exit(0);
			}

			System.out.println("Please enter SQL password.");
			String userPassword = in.nextLine();

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

	public void appLogin(Connection conn) {
		boolean flag = false;
		Scanner in = new Scanner(System.in);


		while (!flag) {
			System.out.println("Welcome to MouseHousing. Type 'login' to connect to an existing "
					+ "profile. Type 'new' to create a new user profile. Type 'q' to exit "
					+ "application.");

			String response = in.nextLine();

			if (response.toLowerCase().compareTo("login") == 0) {
				System.out.println("Please enter app userID.");
				appUserID = in.nextLine();
				System.out.println("Please enter app password.");
				appPassword = in.nextLine();

				try {
					CallableStatement loginStmt = conn.prepareCall("{? = call login(?, ?)}");
					loginStmt.registerOutParameter(1, Types.INTEGER);
					loginStmt.setString(2, appUserID);
					loginStmt.setString(3, appPassword);

					loginStmt.executeQuery();

					int loginResult = loginStmt.getInt(1);


					if (loginResult == -1) {
						System.out.println("Fail");

					}
					else if (loginResult == 1) {
						System.out.println("Success - user");

					}
					else if (loginResult == 2) {
						System.out.println("Success - admin");

					}

				}
				catch (SQLException ex) {
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				}
			}
			else if (response.toLowerCase().compareTo("new") == 0) {

			}
			else if (response.toLowerCase().compareTo("q") == 0) {
				System.exit(0);
			}


		}



	}


	public void programLoop() {
		Connection conn = sqlLogin();

		if (Objects.isNull(conn)) {
			System.out.println("Connection failed to be made.");
			System.exit(1);
		}

		appLogin(conn);


//		try {
//
//		}
//		catch (SQLException ex) {
//			// TODO figure out if actually want these
//			System.out.println("SQLException: " + ex.getMessage());
//			System.out.println("SQLState: " + ex.getSQLState());
//			System.out.println("VendorError: " + ex.getErrorCode());
//		}








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
	}
}
