package finalproject.utilities.gamerole;

import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class MasterGameRole {
	private GameData gameData;
	private Navigation navigation;
	private IGameRole gameRole;
	private Odometer odometer;
	private Shooter shooter;
	private EV3UltrasonicSensor usSensor;
	
	private final double BOX_SIZE;
	
	/**
	 * Public constructor for MasterGameRole.
	 * @param gameData The GameData object that contains all game data.
	 * @param navigation The Navigation object for controlling the robot.
	 */
	public MasterGameRole(GameData gameData, Navigation navigation, Odometer odometer, Shooter shooter, 
			EV3UltrasonicSensor usSensor, double BOX_SIZE) {
		this.gameData = gameData;
		this.navigation = navigation;
		this.odometer = odometer;
		this.shooter = shooter;
		this.usSensor = usSensor;
		this.BOX_SIZE = BOX_SIZE;
	}
	
	/**
	 * Starts playing as the game role specified.
	 */
	public void play() {
		if (gameData.getRole() == GameData.Role.Forward) {
			gameRole = new ForwardGameRole(gameData, navigation, odometer, shooter, BOX_SIZE);
		} else {
			gameRole = new DefenseGameRole(gameData, navigation, odometer, usSensor, shooter, BOX_SIZE);
		}
		
		gameRole.play();
	}
	
	/**
	 * @deprecated Not required for this project.
	 * Travels to the starting corner.
	 * @param gd The GameData object that contains all game data.
	 */
	@SuppressWarnings("unused")
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
