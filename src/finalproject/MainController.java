package finalproject;

import java.util.Map;

import finalproject.objects.GameData;
import finalproject.utilities.LCDInfo;
import finalproject.utilities.localization.*;
import finalproject.utilities.localization.USLocalizer.LocalizationType;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.Shooter;
import finalproject.utilities.WifiConnection;
import finalproject.utilities.gamerole.DefenseGameRole;
import finalproject.utilities.gamerole.ForwardGameRole;
import finalproject.utilities.gamerole.MasterGameRole;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

@SuppressWarnings("rawtypes")
/**
 * This is the main class that will execute all functions of the robot.
 * @author maxsn
 *
 */
public class MainController {
	private static final String SERVER_IP = "192.168.2.24";
	private static final int TEAM_NUMBER = 7;

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
		
		Sound.twoBeeps();
		
		initialize();

		// 1. Get game data from Wi-Fi
//		t.clear();
//		t.drawString("Connecting...", 0, 0);
//		
//		WifiConnection wc = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
//		Map data;
//		GameData gd;
//		
//		try {
//			data = wc.getData();
//			gd = new GameData(data, TEAM_NUMBER);
//		} catch (Exception e) {
//			error(e.getMessage());
//			return;
//		}
//		
//		t.drawString("Game data OK", 0, 1);
		
		// 2. Initialize and localize
		t.drawString("Press ENTER to localize...", 0, 2);
		Button.waitForAnyPress();
		
		t.clear();
		
		lcdInfo = new LCDInfo(odometer);
		localizer = new MasterLocalizer(odometer, midUS, colorSensor, LOCALIZATION_TYPE);
		localizer.localize();
		lcdInfo.stop();
		
		t.clear();
		t.drawString("Localization OK", 0 ,0);
		
		// 3. Play the game
//		MasterGameRole mgr = new MasterGameRole(gd, navigation, odometer, shooter, midUS);
//		mgr.play();
		
		Button.waitForAnyPress();
		shooter.lowerArm();
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
}
