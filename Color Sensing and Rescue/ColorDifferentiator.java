/* Group 30, Razi Murshed -260516333, Mohammad Makkaoui -260451516*/

//class to differentiate between everything else and styrofoam blocks

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;
import lejos.nxt.*;

public class ColorDifferentiator {
	//declaring objects used by the class
	private static UltrasonicSensor usSensor = new UltrasonicSensor(SensorPort.S1);
	private static ColorSensor lightsensor = new ColorSensor(SensorPort.S3);
	
	public ColorDifferentiator()
	{
		//constructor defined
	}

	public boolean runDifferentiator() {

		Delay.msDelay(1000);
		boolean isBlock = false;  
		while (true) {

			while (usSensor.getDistance() < 10) {  // if the distance between object is less than 10 carry ahead
                //gets different values of colors
				int distanceToObject = usSensor.getDistance();
				LCD.drawInt(distanceToObject, 0, 3);
				int dataBlue = lightsensor.getColor().getBlue();
				LCD.drawInt(dataBlue, 0, 0);
				int dataRed = lightsensor.getColor().getRed();
				LCD.drawInt(dataRed, 0, 1);
				double ratio = (dataBlue / dataRed) * 100;   //finds the percentage of blue present
				LCD.drawInt((int) ratio, 0, 2);
				if (distanceToObject < 10) {
					LCD.clear(5);
					LCD.clear(6);
					if ((ratio > 90)) {   //if more than 90% blue its a styrofoam
						LCD.clear(5);
						LCD.clear(6);
						LCD.drawString("Object Detected", 0, 5);

						LCD.drawString("Styrofoam Block", 0, 6);
						isBlock = true;
						return isBlock;
                    //otherwise not the styrofoam
					} else {      
						LCD.clear(5);
						LCD.clear(6);
						LCD.drawString("Object Detected", 0, 5);
						LCD.drawString("Not Styrofoam ", 0, 6);
						isBlock = false;
						return isBlock;
					}
				} else {
					LCD.clear(5);
					LCD.clear(6);
					LCD.drawString("No Object Detected", 0, 5);
					LCD.drawString("Too far!", 0, 6);
				}

			}
			LCD.clear(5);
			LCD.clear(6);
			LCD.drawString("No object", 0, 5);
			return false;
		}

	}
}
