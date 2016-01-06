/******************************************GROUP 30, RAZI MURSHED - 260516333, MOHAMMAD MAKKAOUI - 260451516***********************************/
/**********************************************************CLASS TO DO LIGHT LOCALIZATION******************************************************/
import lejos.nxt.ColorSensor;
import lejos.nxt.*;
/***********************************************************CONSTRUCTOR METHOD*****************************************************************/
public class LightLocalizer {
	//Class variables
	private Odometer odometer;
	private TwoWheeledRobot roboCop;
	private ColorSensor lightSensor;
	//Class constants
	public static double ROTATION_SPEED = 20;
	private double lightValueThreshold = 550;                 // light value threshold, if it drops below 550 sensor over black line
    private Navigation navigator;               // initialize navigator 
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		//Constructor Method
		this.odometer = odo;
		this.roboCop = odo.getTwoWheeledRobot();
		this.lightSensor = ls;
		// turn on the light
		ls.setFloodlight(true);
	}
/************************************************************LIGHT LOCALIZER*******************************************************************/
	public void doLocalization() {
		   double sensorDistance =12.5;//the distance from the light sensor to wheel center
		   //Precaution to move it away from black line if standing on it
		   roboCop.rotate(30);
		   //getting angles for trigonometry
	        int lightValue = lightSensor.getNormalizedLightValue();          
	        // unless the line is black the robot keeps turning
	        while (lightValue>lightValueThreshold){
	            roboCop.setRotationSpeed(ROTATION_SPEED);
	            lightValue = lightSensor.getNormalizedLightValue();
	            LCD.drawInt(lightValue, 0,5);
	        }
	        // if line is black loop breaks
	        double thetaX1 = odometer.getTheta();        //get angle
	        // sleep to catch exceptions
	        try {Thread.sleep(1000);} catch (InterruptedException e) {}
	        lightValue = lightSensor.getNormalizedLightValue();
	        LCD.drawInt(lightValue, 0,5); //Prints values
	        // while line is not black, robot rotates
	        while (lightValue>lightValueThreshold){
	            roboCop.setRotationSpeed(ROTATION_SPEED);
	            lightValue = lightSensor.getNormalizedLightValue();          
	            LCD.drawInt(lightValue, 0,5);  // light sensor reading
	        }
	        // if line is black while loop breaks
	        double thetaY1 = odometer.getTheta();    // get angle
	        // sleep
	        try {Thread.sleep(1000);} catch (InterruptedException e) {}
	        lightValue = lightSensor.getNormalizedLightValue();      
	        LCD.drawInt(lightValue, 0,5); // light sensor reading
	  
	        // while line is not black, robot rotates
	        while (lightValue>lightValueThreshold){
	            roboCop.setRotationSpeed(ROTATION_SPEED);
	            lightValue = lightSensor.getNormalizedLightValue();          
	            LCD.drawInt(lightValue, 0,5); // light sensor reading
	        }
	        // if line is black while loop breaks
	        double thetaX2 =odometer.getTheta();         // get angle
	        // sleep
	        try {Thread.sleep(1000);} catch (InterruptedException e) {}
	        lightValue = lightSensor.getNormalizedLightValue();          
	        LCD.drawInt(lightValue, 0,5); // light sensor reading
	        // while line is not black, robot rotates
	        while (lightValue>lightValueThreshold){
	            roboCop.setRotationSpeed(ROTATION_SPEED);
	            lightValue = lightSensor.getNormalizedLightValue();
	            LCD.drawInt(lightValue, 0,5);
	        }
	        // if line is black while loop breaks
	        Sound.beep();
	        double thetaY2=odometer.getTheta();      // get angle
	        // sleep
	       try {Thread.sleep(1000);} catch (InterruptedException e) {}
	       roboCop.stop();
	        lightValue = lightSensor.getNormalizedLightValue();              
	        LCD.drawInt(lightValue, 0,5); // light sensor reading
	        //starting calculations
	        double thetaX = thetaX2-thetaX1;        // compute difference between x-axis angles
	        double thetaY = thetaY2-thetaY1;        // compute difference between y-axis angles 
	        double updatedX = -sensorDistance*Math.cos(Math.toRadians(thetaY/2)); //calculate new X angle
	        double updatedY = -sensorDistance*Math.cos(Math.toRadians(thetaX/2)); //calculate new Y angle
	        double deltaTheta = (thetaY/2) + 90 - (thetaY2-180); //Theta to be changed
	        double theta = odometer.getTheta();      // get current angle
	        double correctTheta= (theta + deltaTheta);  // calculate corrected theta
	        // Correcting odometer values
	        odometer.setPosition(new double [] {updatedX,updatedY, correctTheta}, new boolean [] {true, true, true});
	        // stop robot motion
	        roboCop.stop();
	        double currentTheta=odometer.getTheta(); // get odometer's value of theta
	        roboCop.rotate(Odometer.minimumAngleFromTo(currentTheta, 90)); // move to 90 degrees from north
	        roboCop.moveForward(-updatedX); //Move to updated X
	        roboCop.rotate(Odometer.minimumAngleFromTo(90, 0)); // Move back north facing
	        roboCop.moveForward(-updatedY); //Move to updated Y
	        roboCop.stop();   // stop robot 
	    } 
	}
/***********************************************************THE END******************************************************************************/

