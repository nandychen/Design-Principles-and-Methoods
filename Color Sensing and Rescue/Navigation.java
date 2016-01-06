/* Group 30, Razi Murshed -260516333, Mohammad Makkaoui -260451516*/

/*
 * Class to run the search and retireve the styrofoam block while avoiding obstacles
 */
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.*;
import lejos.nxt.*;

public class Navigation {
	
	//main navigation class
	//threshold to detect nearby objects
	final static int THRESHOLD = 19;
	final static int FAST = 250, SLOW = 150, ACCELERATION = 4000;  // determine speed and acceleration for the motors
	final static double ERROR_IN_ANGLE = 3.0, ERROR_IN_POSITION = 1.0;  //error in position angle
	private static Odometer odometer;
	private NXTRegulatedMotor leftMotor, rightMotor, crane = Motor.C; //crane is to grab
	private ColorDifferentiator distinguish = new ColorDifferentiator();   //color differentiator object initialized
	private static UltrasonicSensor us;

	public Navigation(Odometer odo, UltrasonicSensor us) {
		//contructor defined
		this.odometer = odo;
		this.us = us;
		NXTRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	public void run() {
		//method to run the robot to its destinations
		
		us.continuous(); // setting the us sensor to continuous mode
		odometer.setPosition(new double[] { 0.0, 0.0, 90.0 }, new boolean[] {true, true, true }); //sets odometers position to {0,0,90}
		double Xpositions[] = { 0, 60, 60,  60,  60,  30,  30,  30,    0,   0,   0 }; //list of destinations in X
		double Ypositions[] = { 0, 45, 120,150, 180, 180, 150, 120,  120, 150, 180 };  //list of destinations in Y
	
		for (int i = 1; i <= 13; i++) {
			//loop to take robot to destinations
			double destinationX = Xpositions[i];   
			double destinationY = Ypositions[i];
			travelTo(destinationX, destinationY);
			if (odometer.getY() > 100) {
				//searches only in second half of the board where the block is likely to be
				turnTo(0, false);
				turnTo(270, false);
				turnTo(180, false);
				turnTo(90, false);
			}
		}
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float motors
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes robot to x and y positions
	 */
	public void travelTo(double x, double y) {
		double minAng;
		boolean gotBlock = false;
		while (Math.abs(x - odometer.getX()) > ERROR_IN_POSITION
				|| Math.abs(y - odometer.getY()) > ERROR_IN_POSITION) {

			double distance = us.getDistance();
			if ((odometer.getY() < 180) || (odometer.getX() < 60)|| (odometer.getX() > 0)) { //prevents detection of walls as objects
				if (distance < THRESHOLD) {  //checks for nearby objects

					stop();
					LCD.drawString("ObjectDetected!", 0, 1);
					Delay.msDelay(1000);
					setSpeeds(SLOW, SLOW);
					moveForward(distance - 8);
					boolean isStyrofoam = distinguish.runDifferentiator(); //checks difference between styrofoam and cinderblock
					if (isStyrofoam == true) { // if styrofoam grabs and goes to final destination
						moveForward(-14);
						grab(-130);
						moveForward(16);
						grab(130);
						gotBlock = true;
						travelToHome(80, 200);
						System.exit(0);

					} else {
						if (isOnWaypoint(x, y) == true) {  //filters for objects on waypoints, if so move to next waypoint

							return;
						}
						boolean wallPoint = DoAvoidance();  //avoiding objects that are not styrofoam blocks
						if (wallPoint == true) {  // if avoidance returns a point on a wall move to next checkpoint

							return;
						}

					}

				} else {

					minAng = (Math.atan2(y - odometer.getY(),x - odometer.getX()))* (180.0 / Math.PI);  //calculations of angle
					if (minAng < 0)
						minAng += 360.0;  // filter for negative angle
					this.turnTo(minAng, false); // if not within angle error turn to angle required
					this.setSpeeds(FAST, FAST);
					/**/
				}

			} else { //its a wall move away and turn around
				moveForward(-10);
				rotate(180);
				return;
			}
		}
		this.setSpeeds(0, 0);
	}

	public void travelToHome(double x, double y) { //travel to method without the us sensor on to take robot to final destination
		double minAng;
		
		while (Math.abs(x - odometer.getX()) > ERROR_IN_POSITION
				|| Math.abs(y - odometer.getY()) > ERROR_IN_POSITION) {
			if((odometer.getX()>60)&&(odometer.getY()>180))
			{
				System.exit(0);
			}
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
					* (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
			
		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {  //turns to desired angle

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > ERROR_IN_ANGLE) {

			double distance = us.getDistance();  //checks for blocks on turning , if present breaks and goes to next travel to
			if (distance < THRESHOLD) {
				Delay.msDelay(200);
				if (distance < THRESHOLD) {
					moveForward(distance - 9);
					break;
				}
			}

			error = angle - this.odometer.getAng();

			if (error < -180.0) {        // else turns to required angle
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}

			if (stop) {
				this.setSpeeds(0, 0);
			}
		}
	}

	//stop robot
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}

	//move forward a certain distance
	public void moveForward(double distance) {
		leftMotor.rotate(convertDistance(2.05, distance), true);
		rightMotor.rotate(convertDistance(2.05, distance), false);

	}
	//taken from square driver in lab 2

	private int convertDistance(double radius, double distance) {
		// tells robot how much to move forward
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private int convertAngle(double radius, double width, double angle) {
		// tells robot how much it should turn in degrees.

		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	//method to grab block desired if found
	public void grab(int angleGrab) {

		crane.setSpeed(100);
		crane.rotate(angleGrab);
		Delay.msDelay(1000);

	}
 
	//avoid cinder blocks
	public boolean DoAvoidance() {
		moveForward(-5);
		int isWallOrBlock = DifferentiateBetweenWallAndBlock();  //check between wall and block
		if (isWallOrBlock == 1) { //if wall turn 180 degrees
			rotate(180);
			return true;
		} else if ((odometer.getX() > 45) && (odometer.getAng() >= 0)       
				&& (odometer.getAng() <= 180)) // to the extreme right facing
												// upwards
		{
			rotate(-90);
			travelTo(odometer.getX() - 35, odometer.getY());
			rotate(90);
			travelTo(odometer.getX(), odometer.getY() + 35);

		} else if ((odometer.getX() < 45) && (odometer.getAng() >= 180)
				&& (odometer.getAng() <= 359)) // normal distance facing
												// downwards
		{
			rotate(-90);
			travelTo(odometer.getX() + 35, odometer.getY());
			rotate(90);
			travelTo(odometer.getX(), odometer.getY() - 35);
		} else if ((odometer.getX() < 45) && (odometer.getAng() >= 0)
				&& (odometer.getAng() <= 180)) // normal distance facing upwards
		{
			rotate(90);
			travelTo(odometer.getX() + 35, odometer.getY());
			rotate(-90);
			travelTo(odometer.getX(), odometer.getY() + 35);
		} else // extreme left facing

		{
			rotate(90);
			travelTo(odometer.getX() - 35, odometer.getY());
			rotate(-90);
			travelTo(odometer.getX(), odometer.getY() + 35);

		}
		return false;

	}

	private int DifferentiateBetweenWallAndBlock() {    // differentiate between wall and block 
		double distanceToObject = us.getDistance();
		if ((odometer.getX() < -10) || (odometer.getX() > 70)
				|| (odometer.getY() > 190)) {
			return 1;
		}

		else
			return 2;

	}

	public static boolean isOnWaypoint(double x, double y) {  //to check if  a block is on a waypoint
		int distanceToBlock = us.getDistance();
		double yposition = odometer.getY() + distanceToBlock;

		double distanceBetweenBlockAndWaypoint = Math.abs(y - yposition);
		if (distanceBetweenBlockAndWaypoint < 10) {
			return true;
		}
		return false;
	}

	public void rotate(double angle) {  //rotates a certain angle
		leftMotor.rotate(convertAngle(2.05, 15.555, angle), true);
		rightMotor.rotate(-convertAngle(2.05, 15.555, angle), false);

	}

}
