/*********************************************NAVIGATE ROBOT CLASS: NAVIGATES THE ROBOT TO DESTINATIONS*************************************/
  //Group 30
  //Razi Murshed 260516333
  //Mohamad Makkaoui 260451516
/*******************************************************************************************************************************************/
import lejos.nxt.*;
/*******************************************************************************************************************************************/ 
public class NavigateRobot extends Thread {
    //Declaring Class Constants
    private static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
    private final int SPEED_FWD = 500;                          // speed of robot when moving forward
    private final int SPEED_ROTATE = 300;                          //  speed of robot when rotating
    private final int ACCELERATION = 500;                           // robot's acceleration
    private final double RIGHT_WHEEL_RADIUS = 2.05;                        // right wheel radius   
    private final double LEFT_WHEEL_RADIUS = 2.05;                         // left wheel radius
    private final double WHEELBASE_WIDTH = 15.255;                          // width from wheel to wheel
    private final double ERROR_IN_ANGLE = 0.1;                        // angle error threshold
    private final double ERROR_IN_POSITION =1;                        // position error threshold
    //Declaring Class Variables
    private static Odometer odometer = new Odometer();              // initializes odometer 
    private static OdometryDisplay display = new OdometryDisplay (odometer);        // initializes odometers's display
    private static double presentTheta;                                 // current value of theta 
    private static boolean isNavigating = false;                        // boolean used for while loop 
          
/********************************************************************************************************************************************/      
    // Making a Constructor
    public NavigateRobot(){ 
        odometer.start();
        display.start();
    }
/********************************************************************************************************************************************/
      //Method to drive the robot to its destinations.
      public void run(){
        // method to take robot to it's waypoints
        travelTo(60,30);
        travelTo(30,30);
        travelTo(30,60);
        travelTo(60,0);
      }
/*********************************************************************************************************************************************/
    //Method to get a boolean value on whether robot is in navigation mode
      public static  boolean isNavigating(){ 
        return isNavigating;
    }
/*********************************************************************************************************************************************/
    //Method that turns robot to desired value.
      public  void turnTo (double theta){
    	isNavigating = true;                    // set boolean isNavigating to true
        double deltaTheta = theta - presentTheta;         // finds change in theta between current position and desired destination
       //Setting the optimal rotation
       if (deltaTheta  < -Math.PI && deltaTheta  >= (-2*Math.PI))            // forcing theta to be within -pi to pi
            {deltaTheta = deltaTheta + Math.PI;}
        else if (deltaTheta  > Math.PI && deltaTheta  <=(2*Math.PI))      // forcing theta to be within -pi to pi
            {deltaTheta = deltaTheta - Math.PI;}
    // set speed of  wheels
      leftMotor.setSpeed(SPEED_ROTATE);                                 
        rightMotor.setSpeed(SPEED_ROTATE);                                
     double angleInDegrees = Math.toDegrees(deltaTheta);                 // convert angle Theta to degrees
        leftMotor.rotate(convertAngle(LEFT_WHEEL_RADIUS, WHEELBASE_WIDTH, angleInDegrees),true);   // rotate wheels until correct heading
        rightMotor.rotate(-convertAngle(RIGHT_WHEEL_RADIUS, WHEELBASE_WIDTH, angleInDegrees), false);   // rotate wheels until correct heading
     }
/*******************************************************************************************************************************************************/
         public void travelTo (double y, double x){
          isNavigating = true;        
           while (isNavigating()){
             //get current position value for coordinates and orientation
            double presentYCoordinate = odometer.getX();
            double presentXCoordinate = odometer.getY();
            presentTheta =  odometer.getTheta();
            // Setting the angle between -pi and pi
            if (presentTheta  < - Math.PI && presentTheta  >= (-2*Math.PI))
                presentTheta = presentTheta + Math.PI;
            else if (presentTheta  > Math.PI && presentTheta  <=(2*Math.PI))
                presentTheta = presentTheta - Math.PI;
            // Calculating the required heading
             double heading = Math.atan2(y - presentYCoordinate, x - presentXCoordinate);
            // Finding required distance to travel
             double distance = Math.sqrt(Math.pow(y - presentYCoordinate, 2) + Math.pow(x - presentXCoordinate, 2));
             // Once robot gets to the desired position, isNavigating returns to false
             //and the next set of instructions can be executed
             if (Math.abs(presentXCoordinate - x) < ERROR_IN_POSITION && Math.abs(presentYCoordinate - y) < ERROR_IN_POSITION){
                 Sound.buzz();
                   isNavigating = false;
                   break;
               }     
               // Making sure robot is facing correct direction
            if (presentTheta  > (heading - ERROR_IN_ANGLE) && (presentTheta < (heading + ERROR_IN_ANGLE) )){
            //Instructs robot to go to desired position
            	 leftMotor.forward();
                rightMotor.forward();
                leftMotor.setAcceleration(ACCELERATION);
                rightMotor.setAcceleration(ACCELERATION);
                leftMotor.setSpeed(SPEED_FWD);
                rightMotor.setSpeed(SPEED_FWD);
                leftMotor.rotate(convertDistance(LEFT_WHEEL_RADIUS, distance), true); 
                rightMotor.rotate(convertDistance(RIGHT_WHEEL_RADIUS, distance), false);
               }
            
            else
             {
                // otherwise robot should turn to correct heading
                 turnTo(heading);
             	
             }
           }
 }
/***************************************************************************************************************************************************/
// methods taken from square driver class of lab 3 
      private  int convertDistance(double radius, double distance) {
        //tells robot how much to move forward
        return  (int)((180.0 * distance) / (Math.PI * radius));
    }
/***************************************************************************************************************************************************/
      
    private  int convertAngle(double radius, double width, double angle) {
        //tells robot how much it should turn in degrees.
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
}
/***********************************************************THE END*********************************************************************************/