import TSim.*;

public class Lab {

  public Lab(Integer speed1, Integer speed2) throws InterruptedException {
    TSimInterface tsi = TSimInterface.getInstance();
    TSimInterface tsi1 = TSimInterface.getInstance();
    
    try {
      tsi.setSpeed(1,speed1);      
      tsi1.setSpeed(2,speed2);
      while(true){
    	SensorEvent event = tsi.getSensor(1);
    	System.out.println(event);
    	SensorEvent event1 = tsi.getSensor(2);
    	System.out.println(event1);
      }
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }
}
