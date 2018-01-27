import TSim.*;

public class Lab {

  public Lab(Integer speed1, Integer speed2) throws InterruptedException {
    TSimInterface tsi = TSimInterface.getInstance();
    TSimInterface tsi1 = TSimInterface.getInstance();
    
    try {
      tsi.setSpeed(1,speed1);      
      tsi1.setSpeed(2,-10);
      tsi1.setSwitch(17, 7, 0);
      tsi1.setSwitch(4, 9, 0);
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

// old code
/*		public void acquireState5(State myState, State otherState) throws InterruptedException, CommandException {
tsi.setSpeed(this.id, 0);
semaphore5c.acquire(1);
System.out.println("train id:"+this.id+". enter 5. mystate:" + myState.getState() + ", otherstate:" + otherState.getState());
if((myState.getState()==3)||(myState.getState()==4)){
	if(otherState.getState()==7 ||(otherState.getState()==8) ){
		tsi.setSwitch(3,11,0);
		tsi.setSwitch(4,9,0);
		tsi.setSpeed(this.id, this.speed);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(6);			
	}
	else{
		tsi.setSwitch(4, 9, 1);
		tsi.setSwitch(3, 11, 1);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(7);
	}
}
else{
	if((otherState.getState()==3)||(otherState.getState()==1)){
		tsi.setSwitch(3,11,1);
		tsi.setSwitch(4,9,0);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(3);					
	}
	else{
		tsi.setSwitch(3,11,0);
		tsi.setSwitch(4,9,0);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(4);				
	}
}
semaphore5c.release(1);

}

public void acquireState8(State myState, State otherState) throws InterruptedException, CommandException{
tsi.setSpeed(this.id, 0);
myState.changeState(8);
semaphore8c.acquire(1);
System.out.println("train id:"+this.id+". enter 8. mystate:" + myState.getState() + ", otherstate:" + otherState.getState());
if((myState.getState()==6)||(myState.getState()==7)){
	if((otherState.getState()==10)||(otherState.getState()==12)){
		tsi.setSwitch(15,9,0);
		tsi.setSwitch(17,7,1);
		tsi.setSpeed(this.id, this.speed);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(9);
	}
	else{
		tsi.setSwitch(15,9,0);
		tsi.setSwitch(17,7,0);
		tsi.setSpeed(this.id,this.speed);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(10);			
	}
}
else{
	if((otherState.getState()==7)||(otherState.getState()==5)){
		tsi.setSwitch(17,7,0);
		tsi.setSwitch(15,9,0);
		tsi.setSpeed(this.id, this.speed);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(6);					
	}
	else{ // modified
		tsi.setSwitch(17,7,0);
		tsi.setSwitch(15,9,1);
		tsi.setSpeed(this.id, this.speed);
		SensorEvent s5c = tsi.getSensor(this.id);
		myState.changeState(7);				
	}
}
semaphore8c.release(1);

}

public void acquireState11(State myState) throws InterruptedException, CommandException {
tsi.setSpeed(this.id, 0);
semaphore11c.acquire(1);
myState.changeState(11);
System.out.println("train id:"+this.id+". enter 11. mystate:" + myState.getState());
tsi.setSpeed(this.id, this.speed);
semaphore11c.release(1);
}*/