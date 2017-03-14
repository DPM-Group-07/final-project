package finalproject.utilities.localization;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * MasterLocalizer is the main localization class that performs both ultrasonic and
 * light localization.
 * @author maxsn
 *
 */
public class MasterLocalizer {
	
	private USLocalizer usLocalizer;
	private LightLocalizer lightLocalizer;
	
	private EV3UltrasonicSensor usSensor;
	private EV3ColorSensor colorSensor;
	private Odometer odometer;
	private Navigation navigation;
	
	/**
	 * Public constructor for MasterLocalizer must be called with valid references to all
	 * dependencies.
	 * @param odometer Main Odometer object for odometry.
	 * @param navigation Main Navigation class for navigation.
	 * @param usSensor A reference to an ultrasonic sensor used for US localization.
	 * @param colorSensor A reference to a color sensor for for light localization.
	 */
	public MasterLocalizer(Odometer odometer, Navigation navigation, EV3UltrasonicSensor usSensor, EV3ColorSensor colorSensor) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.usSensor = usSensor;
		this.colorSensor = colorSensor;
	}
	
	/**
	 * Performs localization. First, ultrasonic localization is performed. Then,
	 * light localization is performed.
	 */
	public void localize() {
		// Pass these through for data collection
		float[] usData = new float[3],
				colorData = new float[3];

		usLocalizer = new USLocalizer(usSensor, odometer, navigation, usData);
		lightLocalizer = new LightLocalizer(colorSensor, odometer, navigation, colorData);

		usLocalizer.doLocalization();
		lightLocalizer.doLocalization();
	}
}
