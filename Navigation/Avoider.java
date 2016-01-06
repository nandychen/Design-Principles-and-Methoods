/*******************************AVOIDER CLASS: AVOIDS OBSTACLES WHILE NAVIGATING*************************************************************/
  //Group 30
  //Razi Murshed 260516333
  //Mohamad Makkaoui 260451516
/*******************************************************************************************************************************************/
import lejos.nxt.*;
public class Avoider extends Thread {
     //Declaring Class Constants
    private static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
    private final int SPEED_FWD = 180;                          // forward speed of robot
    private final int SPPED_ROTATE = 90;                          // rotating speed of robot
    private final double RIGHT_WHEEL_RADIUS = 2.05;                        // right wheel radius   
    private final double LEFT_RADIUS = 2.05;                         // left wheel radius
    private final double WHEELBASE_WIDTH = 15.255;                          // wheelbase
    private final double ERROR_IN_ANGLE = 0.1;                         // angle error threshold
    private final double ERROR_IN_POSITION = 2;                        // position error threshold
    //Declaring Class Variables
    private static Odometer odometer = new Odometer();              // odometer object
    private static OdometryDisplay display = new OdometryDisplay (odometer);        // odometer display
    private static double presentTheta;                             // current theta value
    private static boolean isnavigating = false;                      // boolean used for while loop in TravelTo
    private SensorPort usPort = SensorPort.S1;              // initialize US sensor
    private UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
/*******************************************************************************************************************************************/
  // Making a Constructor
    public Avoider (){ 
        odometer.start();
        display.start();
    }
/*******************************************************************************************************************************************/
public void run(){  
        //robot destination
        travelTo(0,60);
        travelTo(60,0);
  
    }
/*******************************************************************************************************************************************/
// boolean method used for while loop in TravelTo
    public static  boolean isNavigating(){ 
        return isnavigating;
    }
/*******************************************************************************************************************************************/
public  void turnTo (double theta){
          
        isnavigating = true;                  // set boolean isNavigating to true
        double deltaTheta = theta-presentTheta;         // find change in theta between current position and desired destination
          
        //Setting the optimal rotation
        if (deltaTheta  < -Math.PI && deltaTheta  >= (-2*Math.PI))            // force theta to be within -PI and PI
            deltaTheta = deltaTheta + Math.PI;
        else if (deltaTheta  > Math.PI && deltaTheta  <=(2*Math.PI))      // force theta to be with -PI and PI
            deltaTheta = deltaTheta - Math.PI;
        leftMotor.setSpeed(SPPED_ROTATE);                                 // set speed of  wheels
        rightMotor.setSpeed(SPPED_ROTATE);                                //set speed of  wheels
  
      
        double angleInDegrees = Math.toDegrees(deltaTheta);                 // convert angle Theta to degrees
                  
        leftMotor.rotate(convertAngle(LEFT_RADIUS, WHEELBASE_WIDTH, angleInDegrees), true);   // rotate wheels until correct heading
        rightMotor.rotate(-convertAngle(RIGHT_WHEEL_RADIUS, WHEELBASE_WIDTH, angleInDegrees), false);   // rotate wheels until correct heading
}
/*******************************************************************************************************************************************/      
       public void travelTo (double y, double x){
          isnavigating = true;
        int threshold = 20;     // distance away from obstacle
        int usDistance = usSensor.getDistance();        // gets distance from obstacle
        while (isNavigating()){
              // gets current position of robot
            double currentY = odometer.getX();      
            double currentX = odometer.getY();
            presentTheta =  odometer.getTheta();
            // Setting the angle between -pi and pi
            if (presentTheta  < - Math.PI && presentTheta  >= (-2*Math.PI))
                presentTheta = presentTheta + Math.PI;
            else if (presentTheta  > Math.PI && presentTheta  <=(2*Math.PI))
                presentTheta = presentTheta - Math.PI;
      // Calculating the required heading
             double heading = Math.atan2(y - currentY, x - currentX);
             // Finding required distance to travel
             double distance = Math.sqrt(Math.pow(y - currentY, 2) + Math.pow(x - currentX, 2));
          // Once robot gets to the desired position, isNavigating returns to false
           //and the next set of instructions can be executed
             if (Math.abs(currentX - x) < ERROR_IN_POSITION && Math.abs(currentY - y) < ERROR_IN_POSITION){
                 isnavigating = false;
                 break;
             }
            
               
             if (presentTheta > (heading - ERROR_IN_ANGLE) && presentTheta < (heading + ERROR_IN_ANGLE) ){
                   
                // Commands to get robot to desired position
                 leftMotor.forward();                       
                 rightMotor.forward();
                 leftMotor.setSpeed(SPEED_FWD);
                 rightMotor.setSpeed(SPEED_FWD);
                 leftMotor.rotate(convertDistance(LEFT_RADIUS, distance), true); 
                rightMotor.rotate(convertDistance(LEFT_RADIUS, distance), true);
                 usDistance = usSensor.getDistance();
                 if(usDistance <threshold){
                       
                     leftMotor.rotate(convertAngle(LEFT_RADIUS, WHEELBASE_WIDTH, 90), true); 
                        rightMotor.rotate(-convertAngle(RIGHT_WHEEL_RADIUS, WHEELBASE_WIDTH, 90), false);
                        leftMotor.setSpeed(SPEED_FWD);
                        rightMotor.setSpeed(SPEED_FWD);
                        leftMotor.rotate(convertDistance(LEFT_RADIUS, 35), true); 
                        rightMotor.rotate(convertDistance(RIGHT_WHEEL_RADIUS, 35), false);
                        leftMotor.rotate(-convertAngle(LEFT_RADIUS, WHEELBASE_WIDTH, 90),true); 
                        rightMotor.rotate(convertAngle(RIGHT_WHEEL_RADIUS, WHEELBASE_WIDTH, 90),false);
                   }
                 
             } 
              else{
                // otherwise robot should turn to correct heading
                turnTo(heading);
            }
        }
}
/*******************************************************************************************************************************************/        
    // methods taken from square driver 
    private  int convertDistance(double radius, double distance) {
        //tells robot how much to move forward
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }
/*******************************************************************************************************************************************/ 
   private  int convertAngle(double radius, double width, double angle) {
        //tells robot how much it should turn in degrees.
  
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    } 
} 
/***********************************************************THE END*********************************************************************************/