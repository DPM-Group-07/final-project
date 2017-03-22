package finalproject.utilities.gamerole;

/**
 * The interface class for a game role. All game role classes must implement this.
 * @author maxsn
 *
 */
public interface IGameRole {
	/**
	 * The play method which, when called, will drive the robot according to its role
	 * until the game ends.
	 */
	public void play();
}
