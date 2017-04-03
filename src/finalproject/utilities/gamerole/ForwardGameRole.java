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
			pickupBall();
		}
	}
	
	private void pickupBall() {
		
		int clearance = 45;
		int backUp = 20;
		
		// Drive to position
		if (gd.getOmega().equals("N")) {
			navigation.travelTo(clearance, gd.getDispenserPosition().getX());
			navigation.turnTo(0, true);
		} else if (gd.getOmega().equals("E")){
			navigation.travelTo(gd.getDispenserPosition().getY(), clearance);
			navigation.turnTo(90, true);
		} else {
			navigation.travelTo(gd.getDispenserPosition().getY(), 8 - clearance);
			navigation.turnTo(270, true);
		}
		
		shooter.lowerArm();
		Delay.msDelay(500);
		
		navigation.goBackward(-backUp);
		shooter.collect();

		Sound.beep();
		Delay.msDelay(10000);
		
		navigation.goForward(backUp);
		shooter.raiseArmWithBall();
		Delay.msDelay(1000);
		shooter.lowerArm();		
		Button.waitForAnyPress();
		Sound.twoBeeps();
	}
	
	/**
	 * Goes to the dispenser to acquire a ball.
	 */
	private void acquireBall(){
		// Calculates the X and Y position of the dispenser
		double dispenserX = gd.getDispenserPosition().getX() * BOX_SIZE;
		double dispenserY = gd.getDispenserPosition().getY() * BOX_SIZE;
		
		// rightWall should be a value between 10 to 11 * BOX_SIZE. The higher the number, the closer, 
		// the robot is to the right wall. Determine value experimentally
		double rightWall = 10.5 * BOX_SIZE;
		
		// Enable the ultrasonic sensor for obstacle avoidance
		if(!this.usSensor.isEnabled()) this.usSensor.enable();
		
		// Navigate through the exterior field to acquire a ball from the dispenser
		if(dispenserX <= 5 * BOX_SIZE){
			// If the dispenser is located on the North-West wall
			if(dispenserY == 11 * BOX_SIZE){
				//Travel outside of the playing field
				navigation.travelTo(0, 0);
				navigation.travelTo(10.5 * BOX_SIZE, 0);
				navigation.travelTo(10.5 * BOX_SIZE, dispenserX - CLEARANCE);
				navigation.turnTo(270, true);
				adjustPosition();
				askForBall();
				navigation.travelTo(10.5 * BOX_SIZE, 0);
				navigation.travelTo(FORWARD_FIELD_LIMIT, 0);
			}
			// If the dispenser is located on the West wall
			else if(dispenserY >= 0){
				navigation.travelTo(0, 0);
				navigation.travelTo(dispenserY, -BOX_SIZE + CLEARANCE);
				navigation.turnTo(90, true);
				adjustPosition();
				askForBall();
				if(odometer.getY() >= FORWARD_FIELD_LIMIT) navigation.travelTo(FORWARD_FIELD_LIMIT, 0);
			}
		}
		
		// Travel closer to the wall since the right sensor is facing 90 degrees and not 45 degrees
		else{
			// If the dispenser is on the North-East wall
			if(dispenserY == 11 * BOX_SIZE){
				navigation.travelTo(0, rightWall);
				navigation.travelTo(10.5 * BOX_SIZE, rightWall);
				navigation.travelTo(10.5 * BOX_SIZE, dispenserX + CLEARANCE);
				navigation.turnTo(90, true);
				adjustPosition();
				askForBall();
				navigation.travelTo(10.5 * BOX_SIZE, rightWall);
				navigation.travelTo(FORWARD_FIELD_LIMIT, rightWall);
			}
			// If the dispenser is on the East wall
			else if(dispenserY >= 0){
				navigation.travelTo(0, rightWall); 
				navigation.travelTo(dispenserY, 11 * BOX_SIZE - CLEARANCE);
				navigation.turnTo(270, true);
				adjustPosition();
				askForBall();
				if(odometer.getY() >= FORWARD_FIELD_LIMIT) navigation.travelTo(FORWARD_FIELD_LIMIT, rightWall);
			}	
		}
		
		// If the dispenser is on the South wall
		if(dispenserY == -1 * BOX_SIZE){
			navigation.travelTo(dispenserY + CLEARANCE, dispenserX);
			navigation.turnTo(0, true);
			adjustPosition();
			askForBall();
		}
	}
	
	/**
	 * Makes small adjustment to the robot to line up the scoop with the dispenser.
	 */
	private void adjustPosition(){
		shooter.lowerArm();
		navigation.goBackward(-BACK_UP_DISTANCE);
		shooter.collect();
		navigation.goForward(BACK_UP_DISTANCE);
		shooter.raiseArmWithBall();
	}
	
	/**
	 * Asks for the ball
	 */
	private void askForBall(){
		Delay.msDelay(100);
		Sound.beep();
		Delay.msDelay(5000);
	}
	
	/**
	 * Moves into position to launch the ball;
	 */
	private void moveToTarget(){
		shooter.raiseArm();
		
		// Let the robot randomly select a position to fire from
		// Generate a random number from 1 to 4
		Random rand = new Random();
		int randInt = rand.nextInt(4) + 1;
		
		moveToLocation(randInt);
		
		// If the random generation is true, move to a new position to throw off the opponent
		// If not move on to firing
		boolean moveAgain = rand.nextBoolean();
		if(moveAgain){
			randInt = rand.nextInt(4) + 1;
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
			case 1: navigation.travelTo(2 * BOX_SIZE, 3 * BOX_SIZE); break;
			case 2: navigation.travelTo(1.75 * BOX_SIZE, 4 * BOX_SIZE); break;
			case 3: navigation.travelTo(1.75 * BOX_SIZE, 6 * BOX_SIZE); break;
			case 4: navigation.travelTo(2 * BOX_SIZE, 7 * BOX_SIZE); break;
		}
	}
	
	/**
	 * Rotate toward the target and launches the ball at the target.
	 */
	private void shoot() {
		// Aims at the target
		double angle = 90 - (2 * Math.PI / 360) * Math.atan2(10 * BOX_SIZE - odometer.getY(), 
				5 * BOX_SIZE - odometer.getX());
		navigation.turnTo(angle, true);
		
		Delay.msDelay(1000);
		shooter.shoot();
	}
}
