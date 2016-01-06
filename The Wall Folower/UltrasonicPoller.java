import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private UltrasonicController cont;
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
	}
	
	public void run() {
		while (true) {
			//process collected data
			int distanceTo = us.getDistance() ;

			
			
			cont.processUSData(distanceTo);
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
	
