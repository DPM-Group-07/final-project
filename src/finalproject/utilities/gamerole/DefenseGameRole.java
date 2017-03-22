package finalproject.utilities.gamerole;

import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This is the defense class which serves to track the opponent and block shots.
 * @author steven
 */
public class DefenseGameRole implements IGameRole {
	private GameData gd;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private EV3UltrasonicSensor usSensor;
	
	/**
	 * Public constructor for Defense class. Must be called with valid references.
	 * @param gd GameData object for map awareness.
	 * @param navigation Navigation object to navigate across the field.
	 * @param US Ultrasonic sensor object to ping opponent location.
	 * @param odometer Odometer object for odometry.
	 * @param shooter Shooter object to control launch motors.
	 */
	public DefenseGameRole(GameData gd, Navigation navigation, Odometer odometer, EV3UltrasonicSensor usSensor, Shooter shooter){
		this.odometer = odometer;
		this.gd = gd;
		this.navigation = navigation;
		this.shooter = shooter;
		this.usSensor = usSensor;
	}
	
	/**
	 * Main GameRole method that will start the Forward role action cycle.
	 */
	@Override
	public void play() {
		if (!usSensor.isEnabled()) {
			usSensor.enable();
		}
		
		navigation.travelTo(5*30.48, 9*30.48);
		navigation.turnTo(270, false);
		
		// TODO WIP
	}
	
	/**
	 * Tracks the opponent using the ultrasonic sensor.
	 */
	private void trackOpp(){
		// TODO
	}
	
	/**
	 * Raises the launch arm to attempt to block the ball.
	 */
	private void block(){
		shooter.raiseArm();
	}
}
