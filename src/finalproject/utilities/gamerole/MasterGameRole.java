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
		resetOdo(gameData);
		
		if (gameData.getRole() == GameData.Role.Forward) {
			gameRole = new ForwardGameRole(gameData, navigation, odometer, midSensor, shooter, BOX_SIZE);
		} else {
			gameRole = new DefenseGameRole(gameData, navigation, odometer, rightSensor, shooter, BOX_SIZE);
		}
		
		gameRole.play();
	}
	
	/**
	 * Resets the odometer to correct data based on starting corner.
	 * @param gd The GameData object that contains all game data.
	 */
	private void resetOdo(GameData gd) {
		int corner = gd.getStartingCorner();
		boolean[] update = {true, true, true};
		switch(corner){
			case 1: odometer.setPosition(new double[] {0.0, 0.0, 0.0}, update);
					break;
			case 2: odometer.setPosition(new double[] {10 * BOX_SIZE, 0.0, 270.0}, update);
					break;
			case 3: odometer.setPosition(new double[] {10 * BOX_SIZE, 10 * BOX_SIZE, 180.0}, update);
					break;
			case 4: odometer.setPosition(new double[] {0, 10 * BOX_SIZE, 90.0}, update);
					break;
		}
	}
}
