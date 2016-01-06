/* Group 30, Razi Murshed -260516333, Mohammad Makkaoui -260451516*/

//class to localize the robot

import lejos.nxt.*;
import lejos.util.Delay;

public class Localize {

	private static Odometer odometer = new Odometer(Motor.A, Motor.B, 5, true); //initializes objects required
	private static Navigation nav;
	private static UltrasonicSensor us;
    
	public Localize(UltrasonicSensor us){
		//constructor defined
		this.nav = new Navigation(odometer, us);
		this.us = us;
		
	}
	public void localize() {
		 
		
		 //localizes robot to {0,0,90}
		 USLocalizer usl = new USLocalizer(odometer,USLocalizer.LocalizationType.FALLING_EDGE, nav, us); //localizes
		 usl.doLocalization();

		 LCD.clear();
		 odometer.setPosition(new double [] {0.0, 0.0, 90.0}, new boolean [] {true, true, true});
		
		 Delay.msDelay(500);
		
		
	}
		
}
