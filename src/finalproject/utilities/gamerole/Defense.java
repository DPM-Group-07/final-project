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
public class Defense {
	private GameData gd;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private EV3UltrasonicSensor US;
	
	/**
	 * Public constructor for Defense class. Must be called with valid references.
	 * @param gd GameData object for map awareness.
	 * @param navigation Navigation object to navigate across the field.
	 * @param US Ultrasonic sensor object to ping opponent location.
	 * @param odometer Odometer object for odometry.
	 * @param shooter Shooter object to control launch motors.
	 */
	public Defense(GameData gd, Navigation navigation, Odometer odometer, EV3UltrasonicSensor US, Shooter shooter){
		this.odometer = odometer;
		this.gd = gd;
		this.navigation = navigation;
		this.shooter = shooter;
		this.US = US;
	}
	
	/**
	 * Tracks the opponent using the ultrasonic sensor.
	 */
	public void trackOpp(){
		// TODO
	}
	
	/**
	 * Raises the launch arm to attempt to block the ball.
	 */
	public void block(){
		shooter.raiseArm();
	}
}
