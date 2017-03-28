package finalproject.utilities.gamerole;

import java.util.Random;

import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

/**
 * This is the forward class which serves to collect balls from dispense, travel to a 
 * pre-configured launch location and 
 * @author steven
 *
 */
public class ForwardGameRole implements IGameRole {
	// Clearance needed from center of rotation to the scope of the launch arm in cm
	private int CLEARANCE = 30;
	private final double BOX_SIZE;
	
	private GameData gd;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	
	/**
	 * Public constructor for Forward. Must be called with valid references.
	 * @param gd GameData object for map awareness.
	 * @param navigation Navigation object to navigate across the field.
	 * @param odometer Odometer object for odometry.
	 * @param shooter Shooter object to control launch motors.
	 */
	public ForwardGameRole(GameData gd, Navigation navigation, Odometer odometer, EV3UltrasonicSensor usSensor, 
			Shooter shooter, double BOX_SIZE){
		this.gd = gd;
		this.navigation = navigation;
		this.shooter = shooter;
		this.BOX_SIZE = BOX_SIZE;
	}
	
	/**
	 * Main GameRole method that will start the Forward role action cycle.
	 */
	@Override
	public void play() {
		navigation.travelTo(5 * BOX_SIZE, BOX_SIZE);
		navigation.turnTo(90, false);
		
		while(true) {
			acquireBall();
			moveToTarget();
			shoot();
		}
	}
	
	/**
	 * Goes to the dispenser to acquire a ball.
	 */
	private void acquireBall(){
		double dispenserX = gd.getDispenserPosition().getX() * BOX_SIZE;
		double dispenserY = gd.getDispenserPosition().getY() * BOX_SIZE;
		
		switch(gd.getOmega()){
			case "N": navigation.travelTo(dispenserX, dispenserY + CLEARANCE);
					  navigation.turnTo(90, false);
					  break;
			case "S": navigation.travelTo(dispenserX, dispenserY - CLEARANCE);
					  navigation.turnTo(270, false);
					  break;
			case "W": navigation.travelTo(dispenserX - CLEARANCE, dispenserY);
					  navigation.turnTo(0, false);
					  break;
			case "E": navigation.travelTo(dispenserX + CLEARANCE, dispenserY);
					  navigation.turnTo(180, false);
					  break;
		}
		
		shooter.floatArm();
		Sound.setVolume(100);
		Sound.beep();
		
		// Wait for task to complete
		// Needs tweaking
		Delay.msDelay(10000);
	}
	
	/**
	 * Moves into position to launch the ball;
	 */
	private void moveToTarget(){
		shooter.raiseArm();
		
		// Let the robot randomly select a position to fire from
		// Generate a random number from 1 to 6
		Random rand = new Random();
		int randInt = rand.nextInt(6) + 1;
		
		moveToLocation(randInt);
		
		// If the random generation is true, move to a new position to throw off the opponent
		// If not move on to firing
		boolean moveAgain = rand.nextBoolean();
		if(moveAgain){
			randInt = rand.nextInt(6) + 1;
			moveToLocation(randInt);
		}
	}
	
	/**
	 * Moves the robot into position using a Navigation object.
	 * @param pos Integer representation of the location of launch.
	 */
	private void moveToLocation(int pos){
		
		switch(pos){
			case 1: navigation.travelTo(2*BOX_SIZE, 2*BOX_SIZE); break;
			case 2: navigation.travelTo(3*BOX_SIZE, 2*BOX_SIZE/2); break;
			case 3: navigation.travelTo(4*BOX_SIZE, 2*BOX_SIZE/3); break;
			case 4: navigation.travelTo(6*BOX_SIZE, 2*BOX_SIZE/3); break;
			case 5: navigation.travelTo(7*BOX_SIZE, 2*BOX_SIZE/2); break;
			case 6: navigation.travelTo(8*BOX_SIZE, 2*BOX_SIZE); break;
		}
	}
	
	/**
	 * Rotate toward the target and launches the ball at the target.
	 */
	private void shoot() {
		double angle = 2 * Math.PI / 360 * Math.atan2(5 * BOX_SIZE - odometer.getX(), 
				10 * BOX_SIZE - odometer.getY());
		navigation.turnTo(angle, false);
		shooter.shoot();
	}
}
