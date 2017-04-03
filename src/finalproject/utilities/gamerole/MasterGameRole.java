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
	private EV3UltrasonicSensor midSensor, rightSensor;
	
	private final double BOX_SIZE;
	
	/**
	 * Public constructor for MasterGameRole.
	 * @param gameData The GameData object that contains all game data.
	 * @param navigation The Navigation object for controlling the robot.
	 */
	public MasterGameRole(GameData gameData, Navigation navigation, Odometer odometer, Shooter shooter, 
			EV3UltrasonicSensor midSensor, EV3UltrasonicSensor rightSensor, double BOX_SIZE) {
		this.gameData = gameData;
		this.navigation = navigation;
		this.odometer = odometer;
		this.shooter = shooter;
		this.midSensor = midSensor;
		this.rightSensor = rightSensor;
		this.BOX_SIZE = BOX_SIZE;
	}
	
	/**
	 * Starts playing as the game role specified.
	 */
	public void play() {
		if (gameData.getRole() == GameData.Role.Forward) {
			gameRole = new ForwardGameRole(gameData, navigation, odometer, midSensor, shooter, BOX_SIZE);
		} else {
			gameRole = new DefenseGameRole(gameData, navigation, odometer, rightSensor, shooter, BOX_SIZE);
		}
		
		gameRole.play();
	}
}
