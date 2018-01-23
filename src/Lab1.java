import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {

  public Lab1(Integer speed1, Integer speed2) {
    TSimInterface tsi = TSimInterface.getInstance();  

    //Threads
    Semaphore semaphore1 = new Semaphore(1);
    Semaphore semaphore2 = new Semaphore(1);
    Semaphore semaphore5c = new Semaphore(1);
    Semaphore semaphore8c = new Semaphore(1);
    Semaphore semaphore11c = new Semaphore(1);
    Thread t1 = new Thread();
    Thread t2 = new Thread();

    //States
    StateHolder state1 = new StateHolder(14, speed1);
    StateHolder state2 = new StateHolder(1, speed2);

  
    try {
      tsi.setSpeed(2,state2.getSpeed());
      tsi.setSpeed(1,state1.getSpeed());
      t1.start();
      t2.start();
      while(true){
      try 
	{
	//while(true){
    	semaphore1.acquire(1);
	System.out.print("IN");
	System.out.print(state1.getState());

	/*if(state1.getState()==10){
		exceptSpeed(tsi, 1, 0);
	}
*/
        SensorEvent s1 = tsi.getSensor(1);
	if(s1.getStatus()==1){
	int myId = s1.getTrainId(); 
	updateState(state1, state2, s1.getXpos(), s1.getYpos(), myId, tsi, semaphore5c, semaphore8c, semaphore11c);
	}
	System.out.print("OUT");
	System.out.print(state1.getState());
	semaphore1.release(1);
	} 
	//} 
	catch(InterruptedException e)
	{}

	try 
	{
	//while(true){
    	semaphore2.acquire(1);
	System.out.print(state2.getState());
        SensorEvent s2 = tsi.getSensor(1);
	if(s2.getStatus()==1){
	int myId = s2.getTrainId(); 
	updateState(state2, state1, s2.getXpos(), s2.getYpos(), myId, tsi, semaphore5c, semaphore8c, semaphore11c);
	}
	semaphore2.release(1);
	} 
	//}
	catch(InterruptedException e)
	{}
    }
}
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }


  }



public class StateHolder{
    private int state;
    private Integer speed;
    
    public StateHolder(int one, Integer two){
       state = one;
       speed = two;
}
    
    public int getState(){ 
      return state;
}
    public Integer getSpeed(){ 
      return speed;
}

    public void changeSpeed(Integer value){
        this.speed = value;
}

    public void changeState(int value){
        this.state = value;
}
 }    

/*
public StateHolder myState(int id, StateHolder state1, StateHolder state2){
    if(id==1){
    return state1;
    }
    else{
    return state2;
    }
}

public StateHolder otherState(int id, StateHolder state1, StateHolder state2){
    if(id==1){
    return state2;
    }
    else{
    return state1;
    }
}
*/
public void exceptSpeed(TSimInterface tsi, int TrainId, Integer speed){
        try 
	{
	tsi.setSpeed(TrainId,speed);
	} 
	catch(CommandException e)
	{}
}


public void exceptSwitch(TSimInterface tsi, int xPos, int yPos, int state){
        try 
	{
	tsi.setSwitch(xPos, yPos, state);
	} 
	catch(CommandException e)
	{}
}
public void approachStation(TSimInterface tsi, int TrainId, StateHolder myState) throws InterruptedException {
	
	Thread.sleep(1000);
	myState.changeSpeed(-myState.getSpeed());
	exceptSpeed(tsi, TrainId, myState.getSpeed());
}

public void acquireState5(StateHolder myState, StateHolder otherState, TSimInterface tsi, int TrainId, Semaphore semaphore5c) throws InterruptedException, CommandException {
	exceptSpeed(tsi, TrainId, 0);
	semaphore5c.acquire(1);
	if((myState.getState()==3)||(myState.getState()==4)){
		if((otherState.getState()==6)||(otherState.getState()==8)){
			exceptSwitch(tsi,3,11,0);
			exceptSwitch(tsi,1,10,1);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(7);
		}
		else{
			exceptSwitch(tsi,3,11,0);
			exceptSwitch(tsi,1,10,0);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(6);			
		}
	}
	else{
		if((otherState.getState()==3)||(otherState.getState()==1)){
			exceptSwitch(tsi,3,11,1);
			exceptSwitch(tsi,1,10,0);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(3);					
		}
		else{
			exceptSwitch(tsi,3,11,0);
			exceptSwitch(tsi,1,10,0);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(4);				
		}
	}
	semaphore5c.release(1);
	
}

public void acquireState8(StateHolder myState, StateHolder otherState, TSimInterface tsi, int TrainId, Semaphore semaphore8c) throws InterruptedException, CommandException{
	exceptSpeed(tsi, TrainId, 0);
	semaphore8c.acquire(1);
	if(otherState.getState()==11){
		if(TrainId==1){
			tsi.getSensor(2);
		}
		else{
			tsi.getSensor(1);		
		}
	}
	if((myState.getState()==6)||(myState.getState()==7)){
		if((otherState.getState()==10)||(otherState.getState()==12)){
			exceptSwitch(tsi,15,9,0);
			exceptSwitch(tsi,17,7,1);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(9);
		}
		else{
			exceptSwitch(tsi,15,9,0);
			exceptSwitch(tsi,17,7,0);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(10);			
		}
	}
	else{
		if((otherState.getState()==7)||(otherState.getState()==5)){
			exceptSwitch(tsi,17,7,0);
			exceptSwitch(tsi,15,9,0);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(6);					
		}
		else{
			exceptSwitch(tsi,17,7,0);
			exceptSwitch(tsi,15,9,1);
			exceptSpeed(tsi,TrainId,myState.getSpeed());
			SensorEvent s5c = tsi.getSensor(TrainId);
			myState.changeState(7);				
		}
	}
	semaphore8c.release(1);
	
}

public void acquireState11(StateHolder myState, StateHolder otherState, TSimInterface tsi, int TrainId, Semaphore semaphore11c, int x, int y) throws InterruptedException, CommandException {
//x, y ?
	exceptSpeed(tsi, TrainId, 0);
	semaphore11c.acquire(1);
	myState.changeState(11);
	exceptSpeed(tsi, TrainId, myState.getSpeed());
	

/*
	SensorEvent s = tsi.getSensor(TrainId);
	

	if(s.getXpos()==9){
		
		myState.changeState(10);
		System.out.print("bien");
	}
	
	else if(s.getXpos()==7){
		myState.changeState(12);		
	}
	else if(s.getXpos()==8){
			if(s.getYpos()==6){
				myState.changeState(13);
			}
			else{
				myState.changeState(9);
			}
		}		
	

	System.out.print("teeest");

*/
	semaphore11c.release(1);
}

public void updateState(StateHolder stateMyTrain,StateHolder stateOtherTrain,int xPos,int yPos,int TrainId,TSimInterface tsi,Semaphore semaphore5c,Semaphore semaphore8c,Semaphore semaphore11c) throws InterruptedException, CommandException{

    switch(stateMyTrain.getState()){
	case 1:
		stateMyTrain.changeState(3);
		break;
	case 2:
		stateMyTrain.changeState(4);
		break;		
	case 3:
		if(xPos==14){
			approachStation(tsi, TrainId, stateMyTrain);
			stateMyTrain.changeState(1);
			break;
		}
		else{
			acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore5c);
			break;
		}
	
		
	case 4:
		if(xPos==14){
			approachStation(tsi, TrainId, stateMyTrain);
			stateMyTrain.changeState(2);
			break;
		}
		else{
			acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore5c);
			break;
		}
	case 6:
		if(xPos==5){
			acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore5c);
			break;
		}
		else{
			acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore8c);
			break;
		}

	case 7:
		if(xPos==4){
			acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore5c);
			break;
		}
		else{
			acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore8c);
			break;
		}
	case 9:
		if(xPos==17){
			acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore8c);
			break;
		}
		else{
			acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore11c, xPos, yPos);
			break;
		}
	case 10:
		System.out.print("!!!!!!!!!!!!!!!!!!!!!!!");
		if(xPos==16){
			acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore8c);
			break;
		}
		else{
			acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore11c, xPos, yPos);
			break;
		}

	case 11:
		System.out.print("1111111111111111111111 ui bien");
		if(xPos==9){
		
			stateMyTrain.changeState(10);
			System.out.print("bien");
		}
	
		else if(xPos==7){
			stateMyTrain.changeState(12);		
		}
		else if(xPos==8){
				if(yPos==6){
					stateMyTrain.changeState(13);
				}
				else{
					stateMyTrain.changeState(9);
				}
			}		
		break;

	case 12:
		if(xPos==14){
			approachStation(tsi, TrainId, stateMyTrain);
			stateMyTrain.changeState(14);
			break;
		}
		else{
			acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore11c, xPos, yPos);
			break;
		}
	case 13:
		if(xPos==14){
			approachStation(tsi, TrainId, stateMyTrain);
			stateMyTrain.changeState(15);
			break;
		}
		else{
			acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, semaphore11c, xPos, yPos);
			
			break;
		}
	case 14:
		stateMyTrain.changeState(12);
		break;
	case 15:
		stateMyTrain.changeState(13);
		break;
	default:
	   break;
}

/*
public void updateState(StateHolder stateMyTrain,StateHolder stateOtherTrain,int xPos,int yPos,int TrainId,TSimInterface tsi,Integer speed1){
    switch(stateMyTrain.getState()){
       case 1:
          this.exceptSwitch(tsi,3,11,0);
          stateMyTrain.changeState(2);
          break;
       case 2:
          if(xPos==1){
		if(stateOtherTrain.getState()==6){
		     this.exceptSwitch(tsi,4,9,1);
                     stateMyTrain.changeState(5);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,4,9,0);
                     stateMyTrain.changeState(6);
                     break;
               }
          } 
          else{
          stateMyTrain.changeState(1);
          //add wait and go back
	  this.exceptSpeed(tsi,TrainId,speed1);
          break;      
          }
       case 3:
	  this.exceptSwitch(tsi,15,9,0);
          stateMyTrain.changeState(4);
          break;          
       case 4:
          if(xPos==1){
		if(stateOtherTrain.getState()==6){
		     this.exceptSwitch(tsi,4,9,1);
                     stateMyTrain.changeState(5);
                     break;
                     }
		else{
		//stop
		if((stateOtherTrain.getState()==9)||((stateOtherTrain.getState()==11))){
		   while((stateOtherTrain.getState()==9)||(stateOtherTrain.getState()==11)||(stateOtherTrain.getState()==8)){
			this.exceptSpeed(tsi,TrainId,0);
 }
		this.exceptSpeed(tsi,TrainId,speed1);
}
		     this.exceptSwitch(tsi,4,9,0);
                     stateMyTrain.changeState(6);
                     break;
               }
          } 
          else{
          stateMyTrain.changeState(3);
          //add wait and go back
          this.exceptSpeed(tsi,TrainId,speed1); 
          break;      
          }
       case 5:
	    if(xPos==9){

		this.exceptSwitch(tsi,15,9,0);
		stateMyTrain.changeState(8);
		break;
            }
	    else{
		if((stateOtherTrain.getState()==4)||((stateOtherTrain.getState()==2))){
		     this.exceptSwitch(tsi,3,11,0);
                     stateMyTrain.changeState(2);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,3,11,1);
                     stateMyTrain.changeState(4);
                     break;}
	    }
       case 6:
	    if(xPos==9){

		this.exceptSwitch(tsi,15,9,1);
		stateMyTrain.changeState(7);
		break;
            }
	    else{
		if((stateOtherTrain.getState()==4)||((stateOtherTrain.getState()==2))){
		     this.exceptSwitch(tsi,3,11,0);
                     stateMyTrain.changeState(2);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,3,11,1);
                     stateMyTrain.changeState(4);
                     break;}
	    }
       case 7:
	    if(xPos==9){
		this.exceptSwitch(tsi,4,9,1);
		stateMyTrain.changeState(6);
		break;
            }
	    else{
		if((stateOtherTrain.getState()==9)||(stateOtherTrain.getState()==10)){
		     this.exceptSwitch(tsi,17,7,1);
                     stateMyTrain.changeState(11);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,17,7,0);
                     stateMyTrain.changeState(9);
                     break;}
	    }
       case 8:
	    if(xPos==9){
		//stop
		if((stateOtherTrain.getState()==4)||((stateOtherTrain.getState()==2))){
		   while((stateOtherTrain.getState()==4)||((stateOtherTrain.getState()==2))){
			this.exceptSpeed(tsi,TrainId,0);
 }
		this.exceptSpeed(tsi,TrainId,speed1);
}
		this.exceptSwitch(tsi,4,9,1);
		stateMyTrain.changeState(5);
		break;
            }
	    else{
		if((stateOtherTrain.getState()==9)||(stateOtherTrain.getState()==10)){
		     this.exceptSwitch(tsi,17,7,1);
                     stateMyTrain.changeState(11);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,17,7,0);
                     stateMyTrain.changeState(9);
                     break;}
	    }
       case 9:
          if(xPos==19){
		if(stateOtherTrain.getState()==8){
		     this.exceptSwitch(tsi,15,9,0);
                     stateMyTrain.changeState(7);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,15,9,1);
                     stateMyTrain.changeState(8);
                     break;
               }
          } 
          else{
          stateMyTrain.changeState(10);
          //add wait and go back
          this.exceptSpeed(tsi,TrainId,speed1); 
          break;      
          }
       case 10:
	  this.exceptSwitch(tsi,17,7,0);
          stateMyTrain.changeState(9);
          break;
       case 11:
          if(xPos==19){
		if(stateOtherTrain.getState()==8){
		     this.exceptSwitch(tsi,15,9,0);
                     stateMyTrain.changeState(7);
                     break;
                     }
		else{
		     this.exceptSwitch(tsi,15,9,1);
                     stateMyTrain.changeState(8);
                     break;
               }
          } 
          else{
          stateMyTrain.changeState(12);
          //add wait and go back
          this.exceptSpeed(tsi,TrainId,speed1); 
          break;      
          }
       case 12:
	  this.exceptSwitch(tsi,17,7,0);
          stateMyTrain.changeState(11);
          break;
       default:
          break;
}
*/

}
}








