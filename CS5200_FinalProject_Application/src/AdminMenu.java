import java.sql.Connection;

public class AdminMenu implements AdminMenuInterface, UserMenuInterface {
	private Connection conn;

	public AdminMenu(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void menuStart(int userID) {

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
	public void addAddress() {

	}

	@Override
	public void addFacility() {

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
