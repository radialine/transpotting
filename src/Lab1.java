import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {
	private TSimInterface tsi;
	private Semaphore semaphore5c;
	private Semaphore semaphore8c;
	private Semaphore semaphore11c;
	State state1;
	State state2;
	
	public Lab1(Integer speed1, Integer speed2) {
		tsi = TSimInterface.getInstance();

		semaphore5c = new Semaphore(1, true);
		semaphore8c = new Semaphore(1, true);
		semaphore11c = new Semaphore(1, true);

		state1 = new State(14);
		state2 = new State(1);
		new Thread(new Train(1, speed1, state1)).start();
		new Thread(new Train(2, speed2, state2)).start();
	}
	
	class State {
		private int state;
		public State(int state){
			this.state = state;
		}
		public int getState(){ 
			return this.state;
		}

		public void changeState(int value){
			this.state = value;
		}/*
		public Integer getSpeed(){
			return speed;
		}

		public void changeSpeed(Integer value) throws CommandException{
			tsi.setSpeed(this.id, this.speed);
		}
*/
	}

	class Train implements Runnable {
		private int id;
		private int speed;
		private State stateHolder;
		
		public Train(int id,int speed, State state) {
			this.id = id;
			this.speed = speed;
			this.stateHolder = state;
		}

		public int getId(){ return this.id; }
		
		public int getSpeed(){	return this.speed; }
		
		public void changeSpeed(int s) throws CommandException{
			tsi.setSpeed(this.id, s);
		}

		public State getState(){ return this.stateHolder; }
		
		public State chooseState(){
			if (this.id==1){ return state1; }
			else{ return state2; }
		}
		public State chooseOtherState(){
			if (this.id==1){ return state2; }
			else{ return state1; }
		}

		@Override
		public void run() {
			try {
				tsi.setSpeed(this.id, this.speed);
				while(true){
					System.out.println("================================");
					System.out.println("INState for train " + this.id + " " + chooseState().getState());
					SensorEvent s;		
					s = tsi.getSensor(this.id);
					if((s.getStatus()==1)){ 
						updateState(chooseState(), chooseOtherState(), s.getXpos(), s.getYpos(), id);
						System.out.println("OutState for train " + this.id + " " + chooseState().getState());
					}
				}
			} catch (CommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void approachStation(State state) throws InterruptedException, CommandException {
			
			Thread.sleep(1000);
			tsi.setSpeed(this.id, 0);
			Thread.sleep(5000);
			this.changeSpeed(-this.speed);
		}

		public void acquireState5(State myState, State otherState) throws InterruptedException, CommandException {
			tsi.setSpeed(this.id, 0);
			semaphore5c.acquire(1);
			System.out.println("State 5 acquired by Train " + this.id);

			if((myState.getState()==3)||(myState.getState()==4)){
				if(otherState.getState()==7 ||(otherState.getState()==8) ){
					tsi.setSwitch(3,11,0);
					tsi.setSwitch(4,9,0);
					tsi.setSpeed(this.id, this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==7)&&(sp.getYpos()==9)&&(sp.getStatus()==1))){
					
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(6);		
					}	
				
				else{
					tsi.setSwitch(4, 9, 0);
					tsi.setSwitch(3, 11, 1);
					tsi.setSpeed(this.id, this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==4)&&(sp.getYpos()==10)&&(sp.getStatus()==1))){
					
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(7);
				}
			}
			else{
				if((otherState.getState()==3)||(otherState.getState()==1)){
					tsi.setSwitch(3,11,0);
					tsi.setSwitch(4,9,1);
					tsi.setSpeed(this.id, this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==3)&&(sp.getYpos()==12)&&(sp.getStatus()==1))){
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(4);					
				}
				else{
					tsi.setSwitch(3,11,1);
					tsi.setSwitch(4,9,1);
					tsi.setSpeed(this.id,this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==4)&&(sp.getYpos()==11)&&(sp.getStatus()==1))){
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(3);				
				}
			}
			
			semaphore5c.release(1);
			System.out.println("Semaphore 5c released");

		}

		public void acquireState8(State myState, State otherState) throws InterruptedException, CommandException{
			tsi.setSpeed(this.id, 0);
			System.out.println("Trying to get state 8 " + this.id);
			semaphore8c.acquire(1);
			System.out.println("State 8 acquired by Train " + this.id);
			System.out.println("trying to acquire State 8");
			System.out.println("Current state" + myState.getState());

			if((myState.getState()==6)||(myState.getState()==7)){
				if((otherState.getState()==10)||(otherState.getState()==12)||(otherState.getState()==14)){
					tsi.setSwitch(15,9,1);
					tsi.setSwitch(17,7,1);
					tsi.setSpeed(this.id, this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==17)&&(sp.getYpos()==8)&&(sp.getStatus()==1))){
					System.out.println(sp.getXpos() + " " + sp.getYpos() + " " + sp.getStatus());
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(9);
				}
				else{
					tsi.setSwitch(15,9,1);
					tsi.setSwitch(17,7,0);
					tsi.setSpeed(this.id,this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==16)&&(sp.getYpos()==7)&&(sp.getStatus()==1))){
					
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(10);			
				}
			}
			else{
				if((otherState.getState()==7)||(otherState.getState()==5)||(otherState.getState()==3)||(otherState.getState()==4)){
					tsi.setSwitch(17,7,0);
					tsi.setSwitch(15,9,0);
					tsi.setSpeed(this.id,this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==12)&&(sp.getYpos()==9)&&(sp.getStatus()==1))){
					
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(6);				
				}
				else{ // modified
					System.out.println("Wrong path");
					tsi.setSwitch(17,7,0);
					tsi.setSwitch(15,9,1);
					tsi.setSpeed(this.id,this.getSpeed());
					SensorEvent sp = tsi.getSensor(this.id);
					while(!((sp.getXpos()==12)&&(sp.getYpos()==10)&&(sp.getStatus()==1))){
					
					sp = tsi.getSensor(this.id);
					}
					myState.changeState(7);				
				}
			}
			semaphore8c.release(1);
			System.out.println("Semaphore 8c released");

		}

		public void acquireState11(State myState) throws InterruptedException, CommandException {
			tsi.setSpeed(this.id, 0);
			semaphore11c.acquire(1);
			myState.changeState(11);
			tsi.setSpeed(this.id, this.getSpeed());
			semaphore11c.release(1);
		}

		public void updateState(State stateMyTrain,State stateOtherTrain,int xPos,int yPos,int TrainId) throws InterruptedException, CommandException{

			switch(stateMyTrain.getState()){
			case 1:
				stateMyTrain.changeState(3);
				break;
			case 2:
				stateMyTrain.changeState(4);
				break;		
			case 3: 
				if(xPos==14){
					approachStation(stateMyTrain);
					stateMyTrain.changeState(1);
					break;
				}
				//			else if(xPos==4){
				//				break;
				//			}
				else{
					System.out.println("get into state5");
					acquireState5(stateMyTrain, stateOtherTrain);
					break;
				}
			case 4:
				if(xPos==14){
					approachStation(stateMyTrain);
					stateMyTrain.changeState(2);
					break;
				}
				else{
					acquireState5(stateMyTrain, stateOtherTrain);
					break;
				}
			case 6: 
				if(xPos==7){
					acquireState5(stateMyTrain, stateOtherTrain);
					break;
				}
				else{
					acquireState8(stateMyTrain, stateOtherTrain);
					break;
				}

			case 7: // modified
				//			if(xPos==4){
				if(xPos==12){
					acquireState8(stateMyTrain, stateOtherTrain);
					break;
				}
				else{
					System.out.println("++++++++");
					acquireState5(stateMyTrain, stateOtherTrain);
					break;
				}
			case 9:
				if(xPos==17){
					acquireState8(stateMyTrain, stateOtherTrain);
					break;
				}
				else{
					acquireState11(stateMyTrain);
					break;
				}
			case 10:
				if(xPos==16){
					acquireState8(stateMyTrain, stateOtherTrain);
					break;
				}
				else{
					acquireState11(stateMyTrain);
					break;
				}

			case 11:
				if(xPos==9){
					stateMyTrain.changeState(10);
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
					approachStation(stateMyTrain);
					stateMyTrain.changeState(14);
					break;
				}
				else{
					acquireState11(stateMyTrain);
					break;
				}
			case 13:
				if(xPos==14){
					approachStation(stateMyTrain);
					stateMyTrain.changeState(15);
					break;
				}
				else{
					acquireState11(stateMyTrain);
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
		}
	}
}

