package finalproject.utilities.gamerole;

import java.util.Random;

import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.Button;
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
	// Rough clearance needed from center of rotation to the dispenser's mouth in cm
	private final int CLEARANCE = 45;
	// Adjustment distance from robot's scope to dispenser's mouth
	private final int BACK_UP_DISTANCE = 10;
	private final double BOX_SIZE;
	
	private GameData gd;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private EV3UltrasonicSensor usSensor;
	
	private boolean firstShot = true;	
	private final double FORWARD_FIELD_LIMIT;
	
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
		this.usSensor = usSensor;
		this.navigation = navigation;
		this.odometer = odometer;
		this.shooter = shooter;
		this.BOX_SIZE = BOX_SIZE;
		this.FORWARD_FIELD_LIMIT = (10 - gd.getForwardLine()) * BOX_SIZE;
	}
	
	/**
	 * Main GameRole method that will start the Forward role action cycle.
	 */
	@Override
	public void play() {
		while(true) {
			navigation.enableAvoid(true);
			pickupBall();
			navigation.enableAvoid(false);
			moveToTarget();
			shoot();
		}
	}
	
	private void pickupBall() {
		int clearance = 45;
		int backUp = 30;
		
		// Drive to position
		if (gd.getOmega().equals("N")) {
			navigation.travelTo(clearance, gd.getDispenserPosition().getX() * BOX_SIZE);
			navigation.turnTo(0, true);
		} else if (gd.getOmega().equals("E")){
			navigation.travelTo(gd.getDispenserPosition().getY() * BOX_SIZE, clearance);
			navigation.turnTo(90, true);
		} else if (gd.getOmega().equals("W")){
			navigation.travelTo(gd.getDispenserPosition().getY() * BOX_SIZE, 11 * BOX_SIZE - clearance);
			navigation.turnTo(270, true);
		} else {
			navigation.travelTo(gd.getDispenserPosition().getY() * BOX_SIZE - clearance, gd.getDispenserPosition().getX() * BOX_SIZE);
			navigation.turnTo(180, true);
		}
		
		shooter.lowerArm();
		Delay.msDelay(500);
		
		navigation.goBackward(-backUp);
		shooter.collect();

		Sound.beep();
		Delay.msDelay(10000);
		// PICK UP NOW
		
		navigation.goForward(backUp);
		shooter.raiseArmWithBall();
		Delay.msDelay(1000);
	}
	
	/**
	 * Moves into position to launch the ball;
	 */
	private void moveToTarget(){
		// Let the robot randomly select a position to fire from
		// Generate a random number from 1 to 3
		Random rand = new Random();
		int randInt = rand.nextInt(3) + 1;
		
		moveToLocation(randInt);
		
		// If the random generation is true, move to a new position to throw off the opponent
		// If not move on to firing
		boolean moveAgain = rand.nextBoolean();
		if(moveAgain && !firstShot){
			randInt = rand.nextInt(3) + 1;
			moveToLocation(randInt);
		}
	}
	
	/**
	 * Moves the robot into position using a Navigation object. The launch location is roughly 8.3 feet away
	 * target.
	 * @param pos Integer representing the location of launch.
	 */
	private void moveToLocation(int pos){
		switch(pos){
			case 1: navigation.travelTo(2.5 * BOX_SIZE, 3 * BOX_SIZE); break;
			case 2: navigation.travelTo(2 * BOX_SIZE, 5 * BOX_SIZE); break;
			case 3: navigation.travelTo(2.5 * BOX_SIZE, 7 * BOX_SIZE); break;
		}
	}
	
	/**
	 * Rotate toward the target and launches the ball at the target.
	 */
	private void shoot() {
		
		double oAng = odometer.getAng();
		
		// Aims at the target
		double angle = (360 / (2*Math.PI) * Math.atan2(5 * BOX_SIZE - odometer.getY(), 
				10 * BOX_SIZE - odometer.getX()));
		if(angle < 0){
			angle += 360;
		}
		navigation.turnTo(angle, true);
		
		shooter.lowerArm();
		Delay.msDelay(1000);
		shooter.shoot();
		
		firstShot = false;
		Delay.msDelay(3000);
	}
}
