package finalproject;

import java.util.Map;

import finalproject.objects.GameData;
import finalproject.utilities.LCDInfo;
import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.WifiConnection;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
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
	
	private static final TextLCD t = LocalEV3.get().getTextLCD();
		
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
		
		t.clear();
		System.out.println("Connecting...");
		
		// 1. Get game data from Wi-Fi
		WifiConnection wc = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
		Map data;
		GameData gd;
		
		try {
			data = wc.getData();
			gd = new GameData(data, TEAM_NUMBER);
		} catch (Exception e) {
			error(e.getMessage());
			return;
		}
		
		System.out.println("Game data OK");
		
		printGameData(gd);

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

}
