package finalproject;

import java.util.Map;

import finalproject.objects.Coordinate;
import finalproject.objects.GameData;
import finalproject.objects.GameData.Role;
import finalproject.utilities.LCDInfo;
import finalproject.utilities.localization.*;
import finalproject.utilities.localization.USLocalizer.LocalizationType;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import finalproject.utilities.WifiConnection;
import finalproject.utilities.gamerole.BetaGameRole;
import finalproject.utilities.gamerole.IGameRole;
import finalproject.utilities.gamerole.MasterGameRole;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

@SuppressWarnings({ "rawtypes", "unused" })
/**
 * This is the main class that will execute all functions of the robot.
 * @author maxsn
 *
 */
public class MainController {
	private static final String SERVER_IP = "192.168.2.3";
	private static final int TEAM_NUMBER = 7;
	private static final double BOX_SIZE = 30.48;
	
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = false;
	private static final LocalizationType LOCALIZATION_TYPE = LocalizationType.RISING_EDGE;

	// Motor objects
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor leftLaunchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor rightLaunchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	
	// Sensor objects
	private static final EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
	private static final EV3UltrasonicSensor leftUS = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
	private static final EV3UltrasonicSensor midUS = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
	private static final EV3UltrasonicSensor rightUS = new EV3UltrasonicSensor(LocalEV3.get().getPort("S4"));
	
	private static final TextLCD t = LocalEV3.get().getTextLCD();
	
	private static Odometer odometer;
	private static LCDInfo lcdInfo;
	private static Navigation navigation;
	private static MasterLocalizer localizer;
	private static Shooter shooter;
	
	/**
	 * This is the main function that will drive the robot throughout the entire game.
	 * @param args Arguments provided to the main method. This is not used in this application.
	 */
	public static void main(String[] args) {
		// Print message on the LCD screen
		t.clear();
		t.drawString("                ", 0, 0);
		t.drawString("   - TEAM 7 -   ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press ENTER to ", 0, 3);
		t.drawString("      start     ", 0, 4);
		
		// Wait for user input before starting
		while (Button.waitForAnyPress() != Button.ID_ENTER) {
			Delay.msDelay(50);
		}
				
		initialize();

		// 1. Get game data from Wi-Fi
		t.clear();
		t.drawString("Connecting...", 0, 0);
		
		GameData gd;
		WifiConnection wc = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
		Map data;
		
		
		try {
			data = wc.getData();
			gd = new GameData(data, TEAM_NUMBER);
		} catch (Exception e) {
			error(e.getMessage());
			return;
		}
		
		// Test without wifi
//		GameData gd = null;
//		gd = noWifi(gd);
		
		t.drawString("Game data OK", 0, 1);
		Sound.beep();
		
		// 2. Initialize and localize
		t.drawString("Localizing...", 0, 2);
		
		Button.waitForAnyPress();
		Delay.msDelay(1000);
		
		localizer = new MasterLocalizer(odometer, midUS, colorSensor, LOCALIZATION_TYPE);
		localizer.localize();
		
		Sound.beep();
		t.clear();
		System.out.println("Localization OK");

		// 3. Reset odometer to match the figure given in the project description
		resetOdo(gd);
		
		// 4. Play the game
		MasterGameRole mgr = new MasterGameRole(gd, navigation, odometer, shooter, midUS, rightUS, BOX_SIZE);
		mgr.play();
		
		Button.waitForAnyPress();
		System.exit(0);
	}
	
	/**
	 * This function displays an error message and prompts for a key press before
	 * exiting the program.
	 * @param message The error message to be displayed.
	 */
	private static void error(String message) {
		t.clear();
		System.out.println(message);
		
		Sound.twoBeeps();
		Sound.twoBeeps();
		
		System.out.println("Press any button to exit.");
		Button.waitForAnyPress();
		System.exit(1);
	}
	
	/**
	 * Prints game data to standard output.
	 * @param gd The GameData object that contains all game data.
	 */
	private static void printGameData(GameData gd) {
		t.clear();
		System.out.println("Press key for game data");
		Button.waitForAnyPress();
		
		System.out.println("- Begin -");
		System.out.println("Role: " + gd.getRole());
		System.out.println("Corner: " + gd.getStartingCorner());
		System.out.println("Omega: " + gd.getOmega());
		System.out.println("FWD line: " + gd.getForwardLine());
		System.out.println("Press ENTER...");
		
		Button.waitForAnyPress();
		
		System.out.println("DspnsrX: " + gd.getDispenserPosition().getX());
		System.out.println("DspnsrY: " + gd.getDispenserPosition().getY());
		System.out.println("DefzonX: " + gd.getDefenderZone().getX());
		System.out.println("DefzonY: " + gd.getDefenderZone().getY());
		
		System.out.println("- End -");
		System.out.println("Press ENTER...");
	}
	
	/**
	 * Performs various functions related to initialization. Things that should be done
	 * before executing any other task.
	 */
	private static void initialize() {
		// Raise the arm
		shooter = new Shooter(leftLaunchMotor, rightLaunchMotor);
		shooter.raiseArm();
		
		// Instantiate critical utilities
		odometer = new Odometer(leftMotor, rightMotor, 30, true);
		navigation = new Navigation(odometer);
		
		// Disable side sensors and enable middle sensor
		leftUS.disable();
		midUS.enable();
		rightUS.disable();
	}
	
	/**
	 * Resets the odometer to correct data based on starting corner.
	 * @param gd The GameData object that contains all game data.
	 */
	private static void resetOdo(GameData gd){
		int corner = gd.getStartingCorner();
		boolean[] update = {true, true, true};
		switch(corner){
			case 1: odometer.setPosition(new double[] {0.0, 0.0, 0.0}, update);
					break;
			case 2: odometer.setPosition(new double[] {10 * BOX_SIZE, 0.0, 270.0}, update);
					break;
			case 3: odometer.setPosition(new double[] {10 * BOX_SIZE, 10 * BOX_SIZE, 180.0}, update);
					break;
			case 4: odometer.setPosition(new double[] {0, 10 * BOX_SIZE, 90.0}, update);
					break;
		}
	}
	
	/**
	 * To test the robot without wifi
	 * @param gd
	 * @return
	 */
	
	private static GameData noWifi(GameData gd){
		int teamNumber = 7;
		Role role = GameData.Role.Forward;
		int startingCorner = 1;
		int forwardLine = 1;
		int w1 = 2, w2 = 4;
		int bx = 5, by = 5;
		
		Coordinate defenderZone = new Coordinate(w1, w2);
		Coordinate dispenserPosition = new Coordinate(bx, by);
		String omega = "N";	
		
		gd.setTeamNumber(teamNumber);
		gd.setRole(role);
		gd.setStartingCorner(startingCorner);
		gd.setForwardLine(forwardLine);
		gd.setDefenderZone(defenderZone);
		gd.setDispenserPosition(dispenserPosition);
		gd.setOmega(omega);
		
		return gd;
	}
}
