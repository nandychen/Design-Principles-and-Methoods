/******************************************GROUP 30, RAZI MURSHED - 260516333, MOHAMMAD MAKKAOUI - 260451516**************************************/
import lejos.nxt.UltrasonicSensor;
import lejos.util.*;
import lejos.nxt.*;
/**************************************************US LOCALIZER CLASS TO DO ULTRASONIC LOCALIZATION**********************************************/
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
    //Introducing Class variables
	private Odometer odo;
    private TwoWheeledRobot roboCop = new TwoWheeledRobot(Motor.A, Motor.B);
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation navigate;
	public static double distance;
	public static double previousDistance;
/********************************************************CONSTRUCTOR METHOD**********************************************************************/	
	public USLocalizer(Odometer odo, LocalizationType locType, Navigation navigate, UltrasonicSensor us) {
		//Constructor method
		this.odo = odo;
		
		this.us = us;
		this.locType = locType;
		this.navigate = new Navigation(odo, us);
		// switch off the ultrasonic sensor
		us.off();
	}
/****************************************************METHOD TO DO ULTRASONIC LOCALIZATION*******************************************************/
	public void doLocalization() {
	    //Initializing required angles to perform us localization
		double angleA, angleB;
		angleA =0; 
		angleB = 0;
		double deltaTheta=0; //Variable to store change in angle required
            //falling edge Localization
			// Carries out falling edge localization
			  if (locType == LocalizationType.FALLING_EDGE) {
			   distance = getFilteredData();  //distance from wall is obtained  
			   previousDistance = distance;   // storing distance in another variable for future us
			   while(distance < 50){    // if robot starts localization facing wall, rotate until it does not see the wall
			    //roboCop.setRotationSpeed(ROTATION_SPEED);
			    distance = getFilteredData();
			   }
			   // sleep to avoid false positives and negatives 
			   try {Thread.sleep(1000);} 
			   catch (InterruptedException e) {} //Catch exception if any
			   while(distance > 50){  //  robot rotates until it sees a wall
			    // robot rotates until it sees a wall
			   roboCop.setRotationSpeed(ROTATION_SPEED);
			    distance = getFilteredData();
			    }
			   //robot stops when it has seen a wall
			   navigate.stop();
			   angleB = 90-odo.getAng(); //gets the value of theta
			   previousDistance = distance;
			   // sleep 
			   try {Thread.sleep(1000);} catch (InterruptedException e) {} //Catch exception if any
			   // rotate robot other direction until it doesn't see the same wall
			   while(distance < 50){
			    roboCop.setRotationSpeed(-ROTATION_SPEED);
			    distance = getFilteredData();
			   }
			   // sleep 
			   try {Thread.sleep(1000);} catch (InterruptedException e) {} //Catch exception if any
			   //robot rotates until it sees a wall
			   while (distance > 50){
			    roboCop.setRotationSpeed(-ROTATION_SPEED);
			    distance = getFilteredData();
			   }
			  //robot stops as it has seen a wall
			   roboCop.stop();
			   angleA = 90-odo.getAng();  // get the angle A
			   //Start doing calculations from formula in slides 
			   if (angleA > angleB){        //calculating angle to be added to current Theta to fix heading
			    deltaTheta = 225 - ((angleA + angleB)/2); 
			   }else{
			    deltaTheta = 45 - ((angleA + angleB)/2);  
			   }
			   // add value found to current value of theta
			   double correctTheta = (90-odo.getAng()) + deltaTheta;
			   //updates  Odometer
			   odo.setPosition(new double [] {0.0, 0.0, correctTheta}, new boolean [] {false, false, true});
			   //moving robot to an approximate position where it can detect all 4 lines
			   roboCop.rotate(Odometer.minimumAngleFromTo(correctTheta, 0)-5); // robot turns to 0 degrees and faces north
			   
			  } 
		else {
			    // Carries out rising edge localization
			   distance = getFilteredData();  //getting distance
			   previousDistance = distance;   // storing current distance into previous
			   // if robot starts facing a wall
			   if(previousDistance < 35){
			    // robot sees a wall and keeps rotating
			    while(distance < 35){ // if facing wall
			     roboCop.setRotationSpeed(-ROTATION_SPEED);
			     distance = getFilteredData();
			    }
			    try {Thread.sleep(200);} catch (InterruptedException e) {}  //Sleep to avoid false positives and negatives
			    //robot stops when it sees no wall
			    roboCop.stop();
			    angleB = 90-odo.getAng(); //get the value of theta
			    // the robot rotates until it sees a wall
			    while(distance > 35){
			     // robot rotates until it sees a wall
			     roboCop.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			     }
			    //stop robot, it has seen a wall
			    try {Thread.sleep(200);} catch (InterruptedException e) {} //sleep to avoid falses
			    // robot rotates till it sees no wall
			    while(distance < 35){
			     roboCop.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			     }
			    //stop robot, it sees no wall
			    roboCop.stop();
			    angleA =90- odo.getAng(); // capture angle
			    
			   } else {
			    //otherwise if robot didnt see a wall before
			    while(distance > 35){
			     // rotate the robot until it sees a wall now
			     roboCop.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			     }
			    //robot stops as it has seen a wall
			    roboCop.stop();
			    angleB = 90 - odo.getAng(); //get the value of theta 
			    previousDistance = distance; // updates previousdistance
			    try {Thread.sleep(200);} catch (InterruptedException e) {}
			    // robot rotates till it no longer sees a wall
			    while(distance < 35){
			     roboCop.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			    }
			    try {Thread.sleep(200);} catch (InterruptedException e) {} 
			    // stop the robot
			    roboCop.stop();
			    angleA = 90 - odo.getAng(); // get the angle
			   }
			   if (angleA > angleB){        //calculating angle to be added to current Theta
			    deltaTheta = 225 - ((angleA + angleB)/2); 
			   }else{
			    deltaTheta = 45 - ((angleA + angleB)/2);  
			   }
			   double correctTheta = (90-odo.getAng()) + deltaTheta;  // calculate angle to be added to theta
			   // updating  Odometer
			   odo.setPosition(new double [] {0.0, 0.0, correctTheta}, new boolean [] {false, false, true});
			   // robot moves to a position where it can detect all 4 lines
			   roboCop.rotate(Odometer.minimumAngleFromTo(correctTheta, 0)); // robot faces north
			 
		}
	 }
/******************************************************************FILTER METHOD*****************************************************************/
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
				
		return distance;
	}
}
/********************************************************************THE END***********************************************************************/
