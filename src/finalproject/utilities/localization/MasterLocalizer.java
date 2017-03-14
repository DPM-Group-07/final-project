package finalproject.utilities.localization;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class MasterLocalizer {
	
	private USLocalizer usLocalizer;
	private LightLocalizer lightLocalizer;
	
	private EV3UltrasonicSensor usSensor;
	private EV3ColorSensor colorSensor;
	private Odometer odometer;
	private Navigation navigation;
	
	public MasterLocalizer(Odometer odometer, Navigation navigation, EV3UltrasonicSensor usSensor, EV3ColorSensor colorSensor) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.usSensor = usSensor;
		this.colorSensor = colorSensor;
	}
	
	public void localize() {
		// Pass these through for data collection
		float[] leftUSData = new float[3], 
				midUSData = new float[3], 
				rightUSData = new float[3],
				colorData = new float[3];

		usLocalizer = new USLocalizer(usSensor, odometer, navigation, midUSData);
		lightLocalizer = new LightLocalizer(colorSensor, odometer, navigation, colorData);

		usLocalizer.doLocalization();
		lightLocalizer.doLocalization();
	}
}
