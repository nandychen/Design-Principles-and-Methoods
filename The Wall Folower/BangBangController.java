import lejos.nxt.*;
import lejos.util.Delay;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 300;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C, sonicMotor = Motor.B;
	private int distance, prevError;
	private int currentLeftSpeed;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith + 1;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		int error = 0;
		
		error = distance - bandCenter;
	/* Case 1 : Error acceptable */
		
		 if ((Math.abs(error) <= bandwith ) )
		{
			leftMotor.setSpeed(motorStraight); 
			rightMotor.setSpeed(motorStraight);
			
			if ((Math.abs(prevError) <= bandwith ))
			 {
				 leftMotor.setSpeed(motorStraight + 50); 
				rightMotor.setSpeed(motorStraight + 50); //Keep robot going straight
				 
				 
			 }
		}	
		else if ( error < 0) //Case 2: Error Unacceptable: Robot too close to the wall
		{
			if (error < -10) //Case 2: a) Error within -10
			{
            leftMotor.setSpeed(motorHigh+300); 
			rightMotor.setSpeed(motorLow-60); //Rapid turn
			}
			else //Case 2: b) Error negative but unspecified
			{
				leftMotor.setSpeed(motorHigh+280);
				rightMotor.setSpeed(motorLow-60);  //Less rapid turn than Case 2a)
				}
				
			}
		else if ((error >= 0) ) //Case 3: Error Unacceptable: Robot too far from the wall
		{
			if (distance == 255){ //U-turn
		    leftMotor.setSpeed(190); 
			rightMotor.setSpeed(300); //U-turn speeds
			
			}
			else
			{
				leftMotor.setSpeed(150);
				rightMotor.setSpeed(250); //Re-adjusting robot speeds to bring closer to wall	
			}
		}
		else
		{
			leftMotor.stop();
			rightMotor.stop(); //Exceptional circumstances: Stop the robot
		}
		 prevError = error; //Store the current error into previous error
		}
	@Override
	public int readUSDistance() {
		
		
		
		
		
		return this.distance;
	}
}
