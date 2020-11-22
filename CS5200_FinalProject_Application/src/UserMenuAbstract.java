import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public abstract class UserMenuAbstract implements UserMenuInterface{
	protected Connection conn;
	protected Scanner scan;

	protected void addAddressHelper(int userID) {
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

			System.out.println("Please enter facility ID.");
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
		try {
			// TODO show facilites

			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_room(?, ?, ?)}");

			System.out.println("Please enter facility ID of room.");
			int fID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, fID);

			System.out.println("Please enter room number.");
			String roomNum = scan.nextLine();
			callableStatement.setString(2, roomNum);

			System.out.println("Please enter light cycle. (light/dark)");
			String lightCycle = scan.nextLine();
			callableStatement.setString(3, lightCycle);

			if (lightCycle.toLowerCase().compareTo("light") != 0
					&& lightCycle.toLowerCase().compareTo("dark") != 0) {
				System.out.println("Invalid light cycle input.");
				return;
			}

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Room successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			System.out.println("An error occurred while adding the room.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("Provided roomID/facilityID was not an integer.");
		}
	}

	@Override
	public void addRack() {
		try {
			// TODO show rooms

			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_rack(?, ?, ?)}");

			System.out.println("Please enter rackID.");
			int rackID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, rackID);

			System.out.println("Please enter number of cage slots.");
			int slots = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(2, slots);

			System.out.println("Please enter roomID where rack is stored.");
			int roomID = Integer.parseInt(scan.nextLine());

			callableStatement.setInt(3, roomID);

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Rack successfully added to database.\n");
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

	protected void addCageHelper(int uID, boolean breed) {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_cage(?, ?, ?, ?, ?, ?)}");

			System.out.println("Please enter cageID for new cage.");
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

			callableStatement.setInt(5, uID);

			callableStatement.setBoolean(6, breed);

			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Cage successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			System.out.println("An error occurred while adding the cage.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("Provided values where not properly formatted as integers.");
		}
	}

	protected void addMouseHelper(int userID) {
		try {
			CallableStatement callableStatement =
					conn.prepareCall("{CALL new_mouse(?, ?, ?, ?, ?, ?, ?, ?)}");

			System.out.println("Please enter eartag.");
			int earTag = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(1, earTag);

			System.out.println("Please enter genotype abbreviation.");
			String geno = scan.nextLine();
			callableStatement.setString(2, geno);

			System.out.println("Please enter sex.(m/f)");
			String sex = scan.nextLine();
			callableStatement.setString(3, sex);

			System.out.println("Please enter date of birth.(mm-dd-yyyy)");
			String dob = scan.nextLine();
			// get date from string and store in SimpleDateFormat
			SimpleDateFormat smp_dob = new SimpleDateFormat("MM-dd-yyyy");
			// translate from smp_date to util to sql
			java.util.Date util_dob = smp_dob.parse(dob);
			java.sql.Date sql_dob = new java.sql.Date(util_dob.getTime());
			callableStatement.setDate(4, sql_dob);

			String input = "";
			while (input.toLowerCase().compareTo("y") != 0
					&& input.toLowerCase().compareTo("n") != 0) {
				System.out.println("Is the mouse dead? (y/n)");
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
				callableStatement.setDate(5, sql_dod);
			}
			else {
				callableStatement.setNull(5, Types.DATE);
			}

			System.out.println("Please enter cageID where mouse is housed.");
			int cageID = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(6, cageID);

			System.out.println("Please enter origin cage ID.");
			int origin = Integer.parseInt(scan.nextLine());
			callableStatement.setInt(7, origin);

			// TO be used to confirm that the mouse is added to a cage managed by specified user
			callableStatement.setInt(8, userID);

			// TODO check this
			if (callableStatement.executeUpdate() == 1) {
				System.out.println("Cage successfully added to database.\n");
			}
		}
		catch (SQLException e) {
			System.out.println("An error occurred while adding the mouse.");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (NumberFormatException nx) {
			System.out.println("Provided values where not properly formatted as integers.");
		} catch (ParseException e) {
			System.out.println("Error in date formatting.");
		}
	}

	@Override
	public void addGenotype() {

	}

	protected void viewAddressHelper(int userID) {

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

	protected void updateMouseHelper(int userID) {

	}

	protected void updateCageHelper(int userID) {
		// when update to inactive - rack doesnt count against number on rack

	}

	protected void updateAddressHelper(int userID) {

	}

	// Needs to delete address if user is only person there, else remove the address from that user
	protected void deleteAddressHelper(int userID) {

	}

	protected void deleteCageHelper(int userID) {

	}

	protected void deleteMouseHelper(int userID) {

	}

}
