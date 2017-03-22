package finalproject.utilities.gamerole;

import finalproject.objects.GameData;
import finalproject.utilities.Navigation;

public class MasterGameRole {
	private GameData gameData;
	private Navigation navigation;
	
	/**
	 * Public constructor for MasterGameRole.
	 * @param gameData The GameData object that contains all game data.
	 * @param navigation The Navigation object for controlling the robot.
	 */
	public MasterGameRole(GameData gameData, Navigation navigation) {
		this.gameData = gameData;
		this.navigation = navigation;
	}
	
	/**
	 * Starts playing as the game role specified.
	 */
	public void play() {
		goToStart();
	}
	
	/**
	 * Travels to the starting corner.
	 * @param gd The GameData object that contains all game data.
	 */
	private void goToStart(){
		int corner = gameData.getStartingCorner();
		switch(corner){
			case 1: navigation.travelTo(-15.0, -15.0);
					navigation.turnTo(0, false);
					break;
			case 2: navigation.travelTo(304.8 + 15.0, -15.0);
					navigation.turnTo(270, false);
					break;
			case 3: navigation.travelTo(304.8 + 15.0, 304.8 + 15.0);
					navigation.turnTo(270, false);
					break;
			case 4: navigation.travelTo(-15.0, 304.8 + 15.0);
					navigation.turnTo(0, false);
					break;
		}
	}
}
