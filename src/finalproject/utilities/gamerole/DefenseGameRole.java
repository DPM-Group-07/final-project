package finalproject.utilities.gamerole;

import finalproject.objects.Coordinate;
import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

/**
 * This is the defense class which serves to track the opponent and block shots.
 * @author steven
 */
@SuppressWarnings("unused")
public class DefenseGameRole implements IGameRole {
	private GameData gd;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private EV3UltrasonicSensor usSensor;
	
	private final double BOX_SIZE;
	private double bottomY, topY;
	
	private final double CLEARANCE_FROM_FORWARD = 0.5;
	/**
	 * Public constructor for Defense class. Must be called with valid references.
	 * @param gd GameData object for map awareness.
	 * @param navigation Navigation object to navigate across the field.
	 * @param odometer Odometer object for odometry.
	 * @param usSensor Ultrasonic sensor object to ping opponent location.
	 * @param shooter Shooter object to control launch motors.
	 */
	public DefenseGameRole(GameData gd, Navigation navigation, Odometer odometer, EV3UltrasonicSensor usSensor, 
			Shooter shooter, double BOX_SIZE){
		this.odometer = odometer;
		this.gd = gd;
		this.navigation = navigation;
		this.shooter = shooter;
		this.usSensor = usSensor;
		this.BOX_SIZE = BOX_SIZE;
	}
	
	/**
	 * Main GameRole method that will start the Forward role action cycle.
	 */
	@Override
	public void play() {
		if (!usSensor.isEnabled()) {
			usSensor.enable();
		}
		
		getZone();
		block();
		
		navigation.travelTo(5 * BOX_SIZE, (gd.getForwardLine() + CLEARANCE_FROM_FORWARD) * BOX_SIZE);
		navigation.turnTo(90, false);
		
		// TODO WIP
		while(true){
			trackOpp();
		}
	}
	
	/**
	 * Obtains the available play zone for defense
	 */
	private void getZone(){
		bottomY = gd.getForwardLine() * BOX_SIZE;
		topY = (int) gd.getDefenderZone().getY() * BOX_SIZE;
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
