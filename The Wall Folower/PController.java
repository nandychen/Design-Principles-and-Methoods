import lejos.nxt.*;
import lejos.util.Delay;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int filterControl;
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter + 4  ; // Gives smoother turns in p-controller with a greater band center
		this.bandwith = bandwith ;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance variable, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		
		// TODO: process a movement based on the us distance passed in (P style)
		
		int error = distance- bandCenter; 
		
		double gradient = (40*Math.abs(error)); //creating a mathematical function 'gradient' that adjusts speed (fast/slow) of the wheels depending on distance of robot from bandCenter (error)
		
		int fast = motorStraight + (int)gradient; //speed of the faster wheel depending on the turn
        if(fast >= 500){ //limiting the speed of wheel to stay within the given bandwith
            fast = 500;
        }
             
        int slow = motorStraight - ((int)gradient); //speed of the slower wheel depending on the turn
        if(slow <= 220){    //limiting the speed so that the slow speed does not drop to 0
            slow = 220;
        }
      
        if (Math.abs(error) <= bandwith){  //Robot is within acceptable range (bandwidth), therefore change nothing
            Motor.C.setSpeed(motorStraight);
            Motor.A.setSpeed(motorStraight); // keep straight
        } else if (error < 0){               //too close to wall move away  
            Motor.C.setSpeed(slow);
            Motor.A.setSpeed(fast);
        } else { 
        	if(distance == 255)
        	{
        		Delay.msDelay(600);				// a delay that allows robot to avoid the gap and work around it
        		Motor.C.setSpeed(fast);         // too far, return to wall 
                Motor.A.setSpeed(slow);
        	}
            Motor.C.setSpeed(fast);         // too far from wall , move closer
            Motor.A.setSpeed(slow);
        }
    }
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
