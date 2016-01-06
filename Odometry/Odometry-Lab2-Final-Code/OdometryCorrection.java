/* 
 * OdometryCorrection.java
 */

//The purpose of this class is to implement a correction technique using the light sensor to get the final displacement of the robot from a fixed point.

/*
 * Group 30
 * Razi Murshed : 260516333
 * Mohamad Makkaoui : 260451516
 */

import lejos.nxt.*;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	double x,y,theta;
	ColorSensor lightSensor = new ColorSensor(SensorPort.S3);
	double sensorDistance =   -13;  // distance between sensor and wheel center  
	double lightValue = 140; // Value for grid lines

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		 
		

		while (true) {
			correctionStart = System.currentTimeMillis();

		
			//Printing light values

			LCD.drawInt(lightSensor.getLightValue(),7, 8,5 );
			
			
		      if (Motor.A.getSpeed() > (SquareDriver.ROTATE_SPEED - 10 ) && Motor.A.getSpeed() <(SquareDriver.ROTATE_SPEED + 10))
				 {
		                // Do nothing because lines may fall in the path of rotation.
		                  
		          }
				 else
				 {
					// get x, y, theta.
						theta = Math.abs(odometer.getTheta());
						x = odometer.getX();
						y = odometer.getY();
					    
						//When crossing a line.
						if(lightSensor.getLightValue() < lightValue){
							Sound.buzz();
							
							
							if (theta < 0.8){  											
								//y is way above 
								if (y < 25){
									odometer.setY(15-sensorDistance);	
								} else if (y > 30){
									odometer.setY(45-sensorDistance); 
									
								}
							}
							
							else if ( ((theta > 3.2) && (theta < 4.5))) {
					        //y is a lot below 	
							if (y < 25){
									
									odometer.setY(15 + sensorDistance);	
								} else if (y > 30){
									
									odometer.setY(45 + sensorDistance);
								}
							}
							
							else if (theta > 1.5 && theta < 3.0){
								//x is way above 
								if((x < 25)){
								
									odometer.setX(15 - sensorDistance);  
								} else if (x > 30){
						
									odometer.setX(45 - sensorDistance);
									
								}
							}
						
							else if (theta > 4.8 ){
								//x is way below
								if((x < 25)){
									
									odometer.setX(15 + sensorDistance); 
									} else if (x > 30){
										
										odometer.setX(45 + sensorDistance);
									}
								}
						}
			              
			              
			            }
			              
				
			    
				

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}

