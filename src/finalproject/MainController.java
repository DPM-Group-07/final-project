package finalproject;

import java.util.Map;

import finalproject.objects.GameData;
import finalproject.utilities.LCDInfo;
import finalproject.utilities.localization.*;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.WifiConnection;
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
		System.out.println("Connecting...");
		
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
		
		System.out.println("Game data OK");
		
		// 2. Initialize and localize
		System.out.println("Press ENTER to localize...");
		Button.waitForAnyPress();
		
		lcdInfo = new LCDInfo(odometer);
		
		localizer = new MasterLocalizer(odometer, navigation, midUS, colorSensor);
		localizer.localize();
		
		Button.waitForAnyPress();
		
		t.clear();
		System.out.println("Localization OK");

		Button.waitForAnyPress();
		System.exit(0);
	}
	
	/**
	 * This function displays an error message and prompts for a key press before
	 * exiting the program.
	 * @param message The error message to be displayed.
	 */
	private static void error(String message) {
		System.out.println(message);
		System.out.println("Press any button to exit.");
		Button.waitForAnyPress();
		System.exit(1);
	}
	
	/**
	 * Prints game data to standard output.
	 * @param gd The GameData object that contains all game data.
	 */
	private static void printGameData(GameData gd) {
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
	 * Slowly raises the launch arm to the vertical position to reduce robot size.
	 */
	private static void raiseArm() {
		// TODO: Move this to Shooter class.
		leftLaunchMotor.setAcceleration(1000);
		rightLaunchMotor.setAcceleration(1000);
		
		leftLaunchMotor.rotate(90,true);
		rightLaunchMotor.rotate(90,false);
		
		leftLaunchMotor.stop(true);
		rightLaunchMotor.stop();
	}
	
	/**
	 * Performs various functions related to initialization. Things that should be done
	 * before executing any other task.
	 */
	private static void initialize() {
		// Raise the arm
		raiseArm();
		
		// Instantiate critical utilities
		odometer = new Odometer(leftMotor, rightMotor, 30, true);
		navigation = new Navigation(odometer);
		
		// Disable side sensors and enable middle sensor
		leftUS.disable();
		midUS.enable();
		rightUS.disable();
	}
}
