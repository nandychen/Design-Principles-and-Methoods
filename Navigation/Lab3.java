import lejos.nxt.*;
public class Lab3 {
	  
	      
	      
public static void main(String[] args) {
	        
	int buttonChoice;
	          
	          
	        do {
	            // clear the display
	            LCD.clear();
	  
	            // ask the user whether to implement navigator or avoider
	            LCD.drawString("< Left  | Right >", 0, 0);
	            LCD.drawString("        |        ", 0, 1);
	            LCD.drawString("Navigate|Avoider", 0, 2);
	          
	            buttonChoice = Button.waitForAnyPress();
	        }
	        
	        while ((buttonChoice != Button.ID_LEFT) && (buttonChoice != Button.ID_RIGHT));
	        {
	  
	        if (buttonChoice == Button.ID_LEFT) {       // left button starts navigator
	            
	            NavigateRobot Navigator = new NavigateRobot();
	            Navigator.run();
	              
	        } else {
	            // right button starts avoider  
	            Avoider Avoider = new Avoider();
	            Avoider.run();
	              
	            }
	        // exits program 
	        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	        System.exit(0);
	    }
    }
	  
	  
	  
} 


