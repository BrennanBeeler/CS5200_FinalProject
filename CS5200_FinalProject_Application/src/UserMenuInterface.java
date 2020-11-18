public interface UserMenuInterface {
	void menuStart(int userID);

	// TODO make everything private/remove from interface?
	// creating methods
	void addAddress();
	void addFacility();
	void addRoom();
	void addRack();
	void addCage();
	void addMouse();
	void addGenotype();


	// reading methods
	void viewAddress();
	void viewUser();
	void viewFacility();
	void viewRoom();
	void viewRack();
	void viewCage();
	void viewMouse();
	void viewGenotype();

	// update methods
	void updateAddress();
	void updateCage();
	void updateMouse();


	// deleting methods
	void deleteAddress();
	void deleteCage();
	void deleteMouse();
}
