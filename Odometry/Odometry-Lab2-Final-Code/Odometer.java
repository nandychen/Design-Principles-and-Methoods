/*
 * Odometer.java
 */

//The purpose of this class is to implement the odometer into the NXT brick in order to measure distance.

/*
 * Group 30
 * Razi Murshed : 260516333
 * Mohamad Makkaoui : 260451516
 */

import lejos.nxt.*;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	private double leftWheelRadius=2.1;      //New wheel radius
	private double rightWheelRadius=2.1;     
	private double distanceBetweenWheels=15.1695; //New width
	//Declaring necessary Variables
	private double deltaLeftWheelTacho=0.0;
	private double deltaRightWheelTacho=0.0;
	private double previousLeftWheelTacho  =0.0;
	private double previousRightWheelTacho=0.0;
	private double deltaArcLength, deltaTheta;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B; 
	ColorSensor lightSensor = new ColorSensor(SensorPort.S3);

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		
		long updateStart, updateEnd;

		previousLeftWheelTacho=Math.toRadians(leftMotor.getTachoCount());					// intial tachometer reading in radians for left and right wheels. 
		previousRightWheelTacho=Math.toRadians(rightMotor.getTachoCount());

		while (true) {
			updateStart = System.currentTimeMillis();
			
			deltaRightWheelTacho = Math.toRadians(rightMotor.getTachoCount())- previousRightWheelTacho;			// change in tachometer relative to previous values
			previousRightWheelTacho = Math.toRadians(rightMotor.getTachoCount());										// setting last tachometer value equal to present value
			deltaLeftWheelTacho = Math.toRadians(leftMotor.getTachoCount()) - previousLeftWheelTacho;				// change in tachometer relative to previous values	
			previousLeftWheelTacho = Math.toRadians(leftMotor.getTachoCount());										// setting last tachometer value equal to present value
			// computes change in arcLength
			deltaArcLength = ( (deltaRightWheelTacho*rightWheelRadius  + deltaLeftWheelTacho*leftWheelRadius))/2;
			// computes change in angle facing
			deltaTheta = -1*( (deltaRightWheelTacho*rightWheelRadius  - deltaLeftWheelTacho*leftWheelRadius))/distanceBetweenWheels;

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				y =  y +(deltaArcLength * Math.cos(theta + (deltaTheta/2.0) ) );		// determining x-coordinate 
				x = ((x +(deltaArcLength * Math.sin(theta + (deltaTheta/2.0)))));		// determining y-coordinate 
				theta = theta + deltaTheta;											// determining angle facing 
				
		
				
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}
	

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}