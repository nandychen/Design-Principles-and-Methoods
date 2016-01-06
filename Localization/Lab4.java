/******************************************GROUP 30, RAZI MURSHED - 260516333, MOHAMMAD MAKKAOUI - 260451516**************************************/
import lejos.nxt.*;
/**************************************************************MAIN CLASS LAB4********************************************************************/
public class Lab4 {
/******************************************************MAIN METHOD TO RUN THE ROBOT***************************************************************/
	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		//Creates a menu to choose ultrasonic localizer or light localizer
	    int buttonChoice;
        do {
            // clear the display
            LCD.clear();
            // Choose Ultrasonic or Light localization
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString("   US  | light  ", 0, 2);
            buttonChoice = Button.waitForAnyPress();
        } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
  
        if (buttonChoice == Button.ID_LEFT) { 
        	//If you choose US asks you to choose falling or rising edge
            int nextButton; 
            do{
            //clear the display
            LCD.clear();
            // ask to choose between Rising Edge or Falling Edge methods
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString(" Rising| Falling", 0, 2);
            nextButton= Button.waitForAnyPress();
            } while (nextButton != Button.ID_LEFT
                    && nextButton != Button.ID_RIGHT);
              
            if (nextButton == Button.ID_LEFT){
                 //Does rising edge localization
                // initialize sensors and display
                LCDInfo lcd = new LCDInfo(odo);
                UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
                ColorSensor ls = new ColorSensor(SensorPort.S3);
                //initialize navigator
                Navigation navigator = new Navigation(odo);
                USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE, navigator);
                usl.doLocalization();
                } else { 
                	//Do falling edge localization
                    // initialize sensors and display
                    LCDInfo lcd = new LCDInfo(odo);
                    UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
                    LightSensor ls = new LightSensor(SensorPort.S3);
                    //initialize navigator
                    Navigation navigator = new Navigation(odo);
                    USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, navigator);
                    usl.doLocalization();   
                }
        } else {
        	//does Light localization
            // initialize sensors and display
            LCDInfo lcd = new LCDInfo(odo);
            UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
            ColorSensor ls = new ColorSensor(SensorPort.S3);
            //initialize navigator
            Navigation navigator = new Navigation(odo);
            USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, navigator);
            usl.doLocalization();
            //lightSensor localization
            LightLocalizer lsl = new LightLocalizer(odo, ls);
            lsl.doLocalization();   
            navigator.turnTo(0);    
        }
        // exits program 
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);    
    } 
}
/****************************************************************THE END**************************************************************************/



