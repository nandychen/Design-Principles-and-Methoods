/* Group 30, Razi Murshed -260516333, Mohammad Makkaoui -260451516*/

import lejos.nxt.*;
import lejos.util.*;

public class Odometer implements TimerListener {

	private Timer timing;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private final int DEFAULT_TIMEOUT_PERIOD = 20;
	private double LEFT_WHEEL_RADIUS, RIGHT_WHEEL_RADIUS, WHEELBASE_WIDTH;
	private double x, y, theta;
	private double[] oldDH, dDH;
	
	// constructor
	public Odometer (NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, int INTERVAL, boolean autostart) {
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		// values for the wheel base and the wheel width
		this.RIGHT_WHEEL_RADIUS = 2.05;
		this.LEFT_WHEEL_RADIUS = 2.05;
		this.WHEELBASE_WIDTH = 15.455;
		//set initial vlaues to {0,0,90}
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 90.0;
		this.oldDH = new double[2];
		this.dDH = new double[2];

		if (autostart) {
			// if the timeout interval is given as <= 0, default to 20ms timeout 
			this.timing = new Timer((INTERVAL <= 0) ? INTERVAL : DEFAULT_TIMEOUT_PERIOD, this);
			this.timing.start();
		} else
			this.timing = null;
	}
	
	// functions to start/stop the timerlistener
	public void stop() {
		if (this.timing != null)
			this.timing.stop();
	}
	public void start() {
		if (this.timing != null)
			this.timing.start();
	}
	
	/*
	 * Calculates displacement and heading as title suggests
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * LEFT_WHEEL_RADIUS + rightTacho * RIGHT_WHEEL_RADIUS) * Math.PI / 360.0;
		data[1] = (rightTacho * RIGHT_WHEEL_RADIUS - leftTacho * LEFT_WHEEL_RADIUS) / WHEELBASE_WIDTH;
	}
	
	/*
	 * Recompute the odometer values using the displacement and heading changes
	 */
	public void timedOut() {
		this.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (this) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.cos(Math.toRadians(theta));
			y += dDH[0] * Math.sin(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	// return X value
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	// return Y value
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	// return theta value
	public double getAng() {
		synchronized (this) {
			return theta;
		}
	}

	// set x,y,theta
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	// return x,y,theta
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}

	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}
	
	// accessors to motors
	public NXTRegulatedMotor [] getMotors() {
		return new NXTRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}
	public NXTRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	public NXTRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	// static 'helper' methods
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
