package finalproject.objects;

import java.util.Map;

/**
 * This class hold all values that are transmitted to the robot over Wi-Fi at the beginning
 * of the game. This class provides functionality to transform a Map object coming from
 * the WifiConnection class and assign the values to local class variables.
 * @author maxsn
 *
 */
@SuppressWarnings("rawtypes")
public class GameData {
	public static enum Role {
		Forward,
		Defense
	}
	
	private int teamNumber;
	
	private Role role;
	private int startingCorner;
	private int forwardLine;
	private Coordinate defenderZone;
	private Coordinate dispenserPosition;
	private String omega;
	
	/**
	 * Construct a GameData object from a Map object containing all the keys and values
	 * related to the game.
	 * @param data The Map object containing key and value pairs.
	 * @param teamNumber Current team number.
	 * @throws Exception Exception thrown by the mapValues method.
	 */
	public GameData(Map data, int teamNumber) throws Exception {
		this.teamNumber = teamNumber;
		mapValues(data);
	}
	
	/**
	 * Assign class variables to their respective values coming from the Map object.
	 * @param data The Map object containing all key-value pairs (KVPs).
	 * @throws Exception An exception that is thrown if the current team is not
	 * part of the current game.
	 */
	private void mapValues(Map data) throws Exception {
		// Set role and starting corner
		if (((Long)data.get("FWD_TEAM")).intValue() == teamNumber) {
			role = Role.Forward;
			startingCorner = ((Long)data.get("FWD_CORNER")).intValue();
		} else if (((Long)data.get("DEF_TEAM")).intValue() == teamNumber) {
			role = Role.Defense;
			startingCorner = ((Long)data.get("DEF_CORNER")).intValue();
		} else {
			throw new Exception("Team is not part of the current game.");
		}
		
		// Forward line position
		forwardLine = ((Long)data.get("d1")).intValue();
		
		// TODO: Debug here and find out how the coordinates are stored in Map object
		// TODO: Figure out if a String is the best way to represent the omega
		
		// Omega
		omega = (String) data.get("omega");
	}

	/**
	 * Get the team number.
	 * @return The team number
	 */
	public int getTeamNumber() {
		return teamNumber;
	}

	/**
	 * Get the role.
	 * @return The role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Get the starting corner.
	 * @return The starting corner
	 */
	public int getStartingCorner() {
		return startingCorner;
	}

	/**
	 * Get the forward line position.
	 * @return The forward line position
	 */
	public int getForwardLine() {
		return forwardLine;
	}

	/**
	 * Get the defender zone Coordinate object.
	 * @return The defender zone Coordinate object
	 */
	public Coordinate getDefenderZone() {
		return defenderZone;
	}

	/**
	 * Get the dispenser position Coordinate object.
	 * @return The dispenser position Coordinate object
	 */
	public Coordinate getDispenserPosition() {
		return dispenserPosition;
	}

	/**
	 * Get the omega.
	 * @return The omega
	 */
	public String getOmega() {
		return omega;
	}
	
	
}
