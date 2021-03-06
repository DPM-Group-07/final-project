package finalproject.utilities.localization;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.localization.USLocalizer.LocalizationType;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * MasterLocalizer is the main localization class that performs both ultrasonic and
 * light localization.
 * @author maxsn
 *
 */
public class MasterLocalizer {
	
	private ILocalizer usLocalizer;
	private ILocalizer lightLocalizer;
	private LocalizationType localizationType;
	
	private EV3UltrasonicSensor usSensor;
	private EV3ColorSensor colorSensor;
	private Odometer odometer;
	private Navigation navigation;
	
	
	/**
	 * Public constructor for MasterLocalizer must be called with valid references to all
	 * dependencies.
	 * @param odometer Main Odometer object for odometry.
	 * @param usSensor A reference to an ultrasonic sensor used for US localization.
	 * @param colorSensor A reference to a color sensor for for light localization.
	 */
	public MasterLocalizer(Odometer odometer, Navigation navigation, EV3UltrasonicSensor usSensor, 
			EV3ColorSensor colorSensor, LocalizationType localizationType) {
		this.odometer = odometer;
		this.usSensor = usSensor;
		this.navigation = navigation;
		this.colorSensor = colorSensor;
		this.localizationType = localizationType;
	}
	
	/**
	 * Performs localization. First, ultrasonic localization is performed. Then,
	 * light localization is performed.
	 */
	public void localize() {
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];	

		SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
		
		usLocalizer = new USLocalizer(odometer, navigation, usValue, usData, localizationType);
		lightLocalizer = new LightLocalizer(odometer, navigation, colorValue, colorData);

		usLocalizer.doLocalization();
		lightLocalizer.doLocalization();
	}
}
