package finalproject.utilities.gamerole;

import java.util.ArrayList;
import java.util.Collections;

import finalproject.objects.Coordinate;
import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

/**
 * This is the defense class which serves to track the opponent and block shots.
 * @author steven
 */
@SuppressWarnings("unused")
public class DefenseGameRole implements IGameRole {
	public enum Side {WEST, EAST, SOUTH}
	private GameData gd;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private EV3UltrasonicSensor usSensor;
	
	private final double BOX_SIZE;
	private final double WEST_PLAYING_FIELD_LIMIT;
	private final double EAST_PLAYING_FIELD_LIMIT;
	private final double ORIENTATION = 90;
	private final double CLEARANCE_FROM_DEF_ZONE = 1.5;
	private final int FILTER_SIZE = 5;
	private final int MOTOR_SPEED = 240;
	private final double TOP_Y;
	private final Side DISPENSER_SIDE;

	/**
	 * Public constructor for Defense class. Must be called with valid references.
	 * @param gd GameData object for map awareness.
	 * @param navigation Navigation object to navigate across the field.
	 * @param odometer Odometer object for odometry.
	 * @param usSensor Ultrasonic sensor object to ping opponent location.
	 * @param shooter Shooter object to control launch motors.
	 * @param BOX_SIZE Double distance from line to line.
	 */
	public DefenseGameRole(GameData gd, Navigation navigation, Odometer odometer, EV3UltrasonicSensor usSensor,
			Shooter shooter, double BOX_SIZE){
		this.odometer = odometer;
		this.gd = gd;
		this.navigation = navigation;
		this.odometer = odometer;
		this.shooter = shooter;
		this.usSensor = usSensor;
		this.BOX_SIZE = BOX_SIZE;
		this.TOP_Y = (10 - gd.getDefenderZone().getY()) * BOX_SIZE;
		
		if(gd.getDispenserPosition().getY() == -1){
			DISPENSER_SIDE = Side.SOUTH;
		}
		else{
			if(gd.getDispenserPosition().getX() <= 5) DISPENSER_SIDE = Side.WEST;
			else DISPENSER_SIDE = Side.EAST;
		}
		
		
		/* Apply similar triangle algorithm
		 * 
		 * 			/|
		 * 		   / |
		 * 		  /  |
		 * 		 -----   <--- We want this distance
		 * 		/    |
		 *     /     |
		 *    /      |
		 *   _________    
		 */
		
		WEST_PLAYING_FIELD_LIMIT = 2.5;
		EAST_PLAYING_FIELD_LIMIT = 7.5;
	}
	
	/**
	 * Main GameRole method that will start the Forward role action cycle.
	 */
	@Override
	public void play() {
		if (!usSensor.isEnabled()) {
			usSensor.enable();
		}
		
		block();

		// Travels to the left limit or the right limit depending on which side the dispenser is located
		if(DISPENSER_SIDE == Side.WEST){
			navigation.travelTo(TOP_Y - (CLEARANCE_FROM_DEF_ZONE * BOX_SIZE), WEST_PLAYING_FIELD_LIMIT * BOX_SIZE);
			navigation.turnTo(ORIENTATION, true);
		}
		else if(DISPENSER_SIDE == Side.EAST){
			navigation.travelTo(TOP_Y - (CLEARANCE_FROM_DEF_ZONE * BOX_SIZE), EAST_PLAYING_FIELD_LIMIT * BOX_SIZE);
			navigation.turnTo(ORIENTATION, true);
		}
		else{
			navigation.travelTo(TOP_Y - (CLEARANCE_FROM_DEF_ZONE * BOX_SIZE), gd.getDispenserPosition().getX() * BOX_SIZE);
			navigation.turnTo(ORIENTATION, true);
		}
		
		while(true){
			trackOpp();
		}
	}
	

	
	/**
	 * Tracks the opponent using the ultrasonic sensor.
	 */
	private void trackOpp(){
		// The maximum distance is calculated assuming that the opponent plays fair
		// and doesn't try to exploit game rules by weaving in and out of the play zone
		double maxDistance = odometer.getX() - (0.5 * BOX_SIZE);
		double distance = getFilteredData();
		LCD.drawString("" + DISPENSER_SIDE, 0, 4);
		
		// Dispenser is on East wall
		if(DISPENSER_SIDE == Side.EAST){
			if(distance < maxDistance){
				if(odometer.getY() < WEST_PLAYING_FIELD_LIMIT * BOX_SIZE){
					navigation.setSpeeds(MOTOR_SPEED, MOTOR_SPEED);
				}
				else{
					navigation.setSpeeds(-MOTOR_SPEED, -MOTOR_SPEED);
				}
			}
			else{
				if(odometer.getY() > EAST_PLAYING_FIELD_LIMIT * BOX_SIZE){
					navigation.setSpeeds(-MOTOR_SPEED, -MOTOR_SPEED);
				}
				else{
					navigation.setSpeeds(MOTOR_SPEED, MOTOR_SPEED);
				}
			}
		}
		// Dispenser is on West wall
		else if(DISPENSER_SIDE == Side.WEST){
			if(distance < maxDistance){
				if(odometer.getY() < EAST_PLAYING_FIELD_LIMIT * BOX_SIZE){
					navigation.setSpeeds(MOTOR_SPEED, MOTOR_SPEED);
				}
				else{
					navigation.setSpeeds(-MOTOR_SPEED, -MOTOR_SPEED);
				}
			}
			else{
				if(odometer.getY() > (WEST_PLAYING_FIELD_LIMIT * BOX_SIZE)){
					navigation.setSpeeds(-MOTOR_SPEED, -MOTOR_SPEED);
				}
				else{
					navigation.setSpeeds(MOTOR_SPEED, MOTOR_SPEED);
				}
			}
		}

		// Patrol on the imaginary line
		else{
			navigation.travelTo(TOP_Y - (CLEARANCE_FROM_DEF_ZONE * BOX_SIZE), WEST_PLAYING_FIELD_LIMIT * BOX_SIZE);
			navigation.travelTo(TOP_Y - (CLEARANCE_FROM_DEF_ZONE * BOX_SIZE), EAST_PLAYING_FIELD_LIMIT * BOX_SIZE);
		}
		Delay.msDelay(1000);
	}
	
	/**
	 * Raises the arm to block.
	 */
	private void block(){
		shooter.rotateTo(-125);
	}
	
	/**
	 * Obtains usSensor data based on a median filter.
	 * @return Distance of the object.
	 */
	private float getFilteredData(){ 		
		ArrayList<Float> data = new ArrayList<Float>();
		for(int i = 0; i < FILTER_SIZE; i++){
			float[] usData = new float[3];
			usSensor.fetchSample(usData, 0);
			data.add(new Float(usData[0] * 100.0));
			Delay.msDelay(15);
		}
		Collections.sort(data);
		return data.get(FILTER_SIZE/2);
	}
}
