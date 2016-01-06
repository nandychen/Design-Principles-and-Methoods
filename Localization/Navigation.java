/******************************************GROUP 30, RAZI MURSHED - 260516333, MOHAMMAD MAKKAOUI - 260451516***********************************/
/*********************************************NAVIGATION ROBOT CLASS: NAVIGATES THE ROBOT TO DESTINATIONS**************************************/
  //Group 30
  //Razi Murshed 260516333
  //Mohamad Makkaoui 260451516
/*******************************************************************************************************************************************/
import lejos.nxt.*;
/*******************************************************************************************************************************************/ 
public class Navigation extends Thread {
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
    private static double presentTheta;                                 // current value of theta 
    private static boolean isNavigating = false;                        // boolean used for while loop 
    private Odometer odometer;
    private TwoWheeledRobot roboCop;
          
/********************************************************************************************************************************************/      
    // Making a Constructor
    public Navigation(Odometer odo){ 
        this.odometer = odo;
        this.roboCop = odo.getTwoWheeledRobot();
        
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
      roboCop.setRotationSpeed(SPEED_ROTATE);                            
     double angleInDegrees = Math.toDegrees(deltaTheta);                 // convert angle Theta to degrees
     roboCop.rotate(angleInDegrees); 
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
            	
                roboCop.setForwardSpeed(SPEED_FWD);
                roboCop.moveForward(distance);
            	
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