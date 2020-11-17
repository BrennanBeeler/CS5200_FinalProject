import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class Controller {

	// TODO move into view?
	private Connection login() {
		final String database = "jdbc:mysql://localhost:3306/mouse_housing?characterEncoding=UTF-8&useSSL=false";
		Connection conn = null;
		boolean connFlag = false;

		while (!connFlag) {
			// Create scanner and use to get username and password
			Scanner in = new Scanner(System.in);
			System.out.println("Please enter username. Type q to quit.");
			String userName = in.nextLine();

			if (userName.toLowerCase().compareTo("q") == 0) {
				System.exit(0);
			}

			System.out.println("Please enter password.");
			String userPassword = in.nextLine();

			// try to connect to server
			try {
				conn = DriverManager.getConnection(database, userName, userPassword);

				if (conn.isValid(5)) {
					connFlag = true;
				}
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


	public void programLoop() {
		Connection conn = login();

		if (Objects.isNull(conn)) {
			System.out.println("Connection failed to be made.");
			System.exit(1);
		}

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
