/* Group 30, Razi Murshed -260516333, Mohammad Makkaoui -260451516*/

//Main method here
import lejos.nxt.*;
import lejos.util.Delay;

public class Lab5 {
	  
	      
	      
public static void main(String[] args) {
	        
	            //main method
	        	TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B); //Initializes TwoWheeledRobot object
	        	Odometer odometer = new Odometer(Motor.A, Motor.B, 5, true); //Initializes odometer for robot
	        	 UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1); // Initializes us sensor
	            Navigation navigator = new Navigation(odometer,us); // Initializes navigation objects
	            LCDInfo lcd = new LCDInfo(odometer); //Display of LCD
	            Localize local = new Localize(us); // Localizer object
                local.localize(); // Command to cause robot to do ultrasonic localization
	            navigator.run();  //runs search algorithm for robot
	
	  
	  
} 
}

