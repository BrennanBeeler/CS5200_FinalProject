import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Controller {
	public static void main(String[] args) {
		Properties connectionProps = new Properties();
		connectionProps.put("user", "root");
		connectionProps.put("password", "orbitmyHead93");
		// String database = "jdbc:mysql://localhost:3306/mouse_housing?characterEncoding=UTF-8&useSSL=false", ;

		Connection conn = null;

		try {

			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mouse_housing?characterEncoding=UTF-8&useSSL=false", connectionProps);

			// TODO

			System.out.println("Hey we doing stuff");

			conn.close();
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
}
