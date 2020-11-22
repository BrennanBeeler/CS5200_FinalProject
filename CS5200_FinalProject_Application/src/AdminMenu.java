import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminMenu extends UserMenuAbstract implements AdminMenuInterface {

	public AdminMenu(Connection conn) {
		this.conn = conn;
		this.scan = new Scanner(System.in);
	}

	@Override
	public void addFacilityAccess() {

	}

	@Override
	public void viewFacilityAccess() {

	}

	@Override
	public void updateUser() {

	}

	@Override
	public void updateFacility() {

	}

	@Override
	public void updateRoom() {

	}

	@Override
	public void updateRack() {

	}

	@Override
	public void updateGenotype() {

	}

	@Override
	public void deleteFacilityAccess() {

	}

	@Override
	public void deleteUser() {

	}

	@Override
	public void deleteFacility() {

	}

	@Override
	public void deleteRoom() {

	}

	@Override
	public void deleteRack() {

	}

	@Override
	public void deleteGenotype() {

	}

	@Override
	public void menuStart(int userID) {
		viewUser();
	}

	@Override
	public void addAddress() {

	}

	@Override
	public void addCage() {

	}

	@Override
	public void addMouse() {

	}

	@Override
	public void viewAddress() {

	}

	// TODO: needs to be tested
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
			System.out.println("An error occurred while getting user data.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("Provided values where not properly formatted as integers.");
		}

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
