package finalproject.utilities.gamerole;

import finalproject.objects.Coordinate;
import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.Button;
import lejos.utility.Delay;

/**
 * This game role class contains all procedures related to the beta demo.
 * @author maxsn
 *
 */
@SuppressWarnings("unused")
public class BetaGameRole implements IGameRole {
	
	private static final double ROBOT_NOSE_CLEARANCE = 0.5;
	private static final double SQUARE_SIZE = 30.48;
	
	private static final int TARGET_X = 5;
	private static final int TARGET_Y = 6;
	
	private GameData gameData;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private Coordinate targetCoordinate;
	
	/**
	 * Public constructor for this game role.
	 * @param gameData GameData object containing all game data values for this round.
	 * @param navigation Navigation object for navigation.
	 * @param odometer Odometer object for odometry.
	 * @param shooter Shooter object for shooting.
	 */
	public BetaGameRole(GameData gameData, Navigation navigation, Odometer odometer, Shooter shooter) {
		this.gameData = gameData;
		this.navigation = navigation;
		this.odometer = odometer;
		this.shooter = shooter;
		
		targetCoordinate = new Coordinate(TARGET_X, TARGET_Y);
	}

	/**
	 * This method implements the play() method, the entry point for this game role.
	 */
	@Override
	public void play() {
		// 1. Go to shooting position
		navigation.travelTo((targetCoordinate.getY() - 4 - ROBOT_NOSE_CLEARANCE) * SQUARE_SIZE, targetCoordinate.getX() * SQUARE_SIZE);
		navigation.turnTo(0.0, true);
		Delay.msDelay(1000);
		
		// 2. Shoot
		shooter.lowerArm();
		shooter.shoot();
		
		// Debug
//		while (Button.waitForAnyPress() == Button.ID_UP) {
//			Delay.msDelay(2000);
//			shooter.shoot();
//		}
	}
}
