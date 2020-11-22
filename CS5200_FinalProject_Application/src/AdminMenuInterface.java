public interface AdminMenuInterface extends UserMenuInterface{
	// creating methods
	void addFacilityAccess();

	// reading methods


	// update methods
	void updateUser();
	void updateFacility();
	void updateRoom();
	void updateRack();
	void updateGenotype();

	// deleting methods
	void deleteFacilityAccess();
	void deleteUser();
	void deleteFacility();
	void deleteRoom();
	void deleteRack();
	void deleteGenotype();
}
