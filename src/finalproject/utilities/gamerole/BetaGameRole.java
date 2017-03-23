package finalproject.utilities.gamerole;

import finalproject.objects.Coordinate;
import finalproject.objects.GameData;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class BetaGameRole implements IGameRole {
	
	private static final double ROBOT_NOSE_CLEARANCE = 0.5;
	private static final double SQUARE_SIZE = 30.48;
	
	private static final int TARGET_X = 2;
	private static final int TARGET_Y = 6;
	
	private GameData gameData;
	private Navigation navigation;
	private Odometer odometer;
	private Shooter shooter;
	private Coordinate targetCoordinate;
	
	public BetaGameRole(GameData gameData, Navigation navigation, Odometer odometer, Shooter shooter) {
		this.gameData = gameData;
		this.navigation = navigation;
		this.odometer = odometer;
		this.shooter = shooter;
		
		targetCoordinate = new Coordinate(TARGET_X, TARGET_Y);
	}

	@Override
	public void play() {
		// 1. Go to shooting position
		navigation.travelTo((targetCoordinate.getY() - gameData.getForwardLine() - ROBOT_NOSE_CLEARANCE) * SQUARE_SIZE, targetCoordinate.getX() * SQUARE_SIZE);
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
