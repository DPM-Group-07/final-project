package finalproject.utilities.gamerole;

import java.util.Random;

import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.Sound;
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
	private double SQUARE_SIZE = 30.48;
	
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
	public ForwardGameRole(GameData gd, Navigation navigation, Odometer odometer, Shooter shooter){
		this.gd = gd;
		this.navigation = navigation;
		this.shooter = shooter;
	}
	
	/**
	 * Main GameRole method that will start the Forward role action cycle.
	 */
	@Override
	public void play() {
		navigation.travelTo(5*30.48, 30.48);
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
		double dispenserX = gd.getDispenserPosition().getX() * SQUARE_SIZE;
		double dispenserY = gd.getDispenserPosition().getY() * SQUARE_SIZE;
		
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
		// TODO implement Shooter class
		shooter.lowerArm();
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
		// TODO implement Shooter class
		shooter.raiseArmToMove();
		
		// Let the robot randomly select a position to fire from
		// Generate a random number from 1 to 6
		Random rand = new Random();
		int randInt = rand.nextInt(6) + 1;
		
		switch(randInt){
			case 1: navigation.travelTo(2*SQUARE_SIZE, SQUARE_SIZE); break;
			case 2: navigation.travelTo(3*SQUARE_SIZE, SQUARE_SIZE/2); break;
			case 3: navigation.travelTo(4*SQUARE_SIZE, SQUARE_SIZE/3); break;
			case 4: navigation.travelTo(6*SQUARE_SIZE, SQUARE_SIZE/3); break;
			case 5: navigation.travelTo(7*SQUARE_SIZE, SQUARE_SIZE/2); break;
			case 6: navigation.travelTo(8*SQUARE_SIZE, SQUARE_SIZE); break;
		}
	}
	
	/**
	 * Rotate toward the target and launches the ball at the target.
	 */
	private void shoot() {
		double angle = 2 * Math.PI / 360 * Math.atan2(5 * SQUARE_SIZE - odometer.getX(), 
				10 * SQUARE_SIZE - odometer.getY());
		navigation.turnTo(angle, false);
		shooter.shoot();
	}
}
