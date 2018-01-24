import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {

	public Lab1(Integer speed1, Integer speed2) {
		TSimInterface tsi = TSimInterface.getInstance();

		//Threads
		Semaphore semaphore5c = new Semaphore(1);
		Semaphore semaphore8c = new Semaphore(1);
		Semaphore semaphore11c = new Semaphore(1);

		State state1 = new State(14, speed1, tsi);
		State state2 = new State(1, speed2, tsi);
		new Thread(new Train(1, tsi,semaphore5c, semaphore8c, semaphore11c)).start();
		new Thread(new Train(2, tsi,semaphore5c, semaphore8c, semaphore11c)).start();
	}
	class State {
		private int state;
		private int speed;
		private TSimInterface tsi;
		public State(int state, int speed, TSimInterface tsi){
			this.state=state;
			this.speed = speed;
		}
		public int getState(){ 
			return this.state;
		}
		public void changeState(int value){
			this.state = value;
		}
		public Integer getSpeed(){ 
			return speed;
		}

		public void changeSpeed(Integer value) throws CommandException{
			this.tsi.setSpeed(this.id, this.speed);
		}
		
	}

	class Train implements Runnable {
		private int id;
		private TSimInterface tsi; 

		public Train(int id, TSimInterface tsi, Semaphore semaphore5c,Semaphore semaphore8c, Semaphore semaphore11c) {
			this.id = id;
			this.tsi = tsi;
		}

		public int getId(){
			return id;
		}


		public State chooseState(int id, State state1, State state2){
			if (id==1){
				return state1;
			}
			else{
				return state2;
			}
		}
		public State chooseOtherState(int id, State stete1, State state2){
			if (id==1){
				return state2;
			}
			else{
				return state1;
			}
		}

		@Override
		public void run() {
			while(true){
				SensorEvent s = tsi.getSensor(this.id);
				if(s.getStatus()==1){ 
					updateState(chooseState(this.id, state1, state2), chooseOtherState(this.id, state1, state2), s.getXpos(), s.getYpos(), id, this.tsi, semaphore5c, semaphore8c, semaphore11c);
				}
			}
		}

		public void approachStation(State state) throws InterruptedException, CommandException {
			Thread.sleep(1000);
			state.changeSpeed(-this.getSpeed());
		}

		public void acquireState5(State myState, State otherState,Semaphore semaphore5c) throws InterruptedException, CommandException {
			tsi.setSpeed(this.id, 0);
			semaphore5c.acquire(1);

			if((myState.getState()==3)||(myState.getState()==4)){
				if(otherState.getState()==7 ||(otherState.getState()==8) ){
					tsi.setSwitch(3,11,0);
					tsi.setSwitch(4,9,0);
					tsi.setSpeed(this.id, myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(6);			
				}
				else{
					tsi.setSwitch(4, 9, 1);
					tsi.setSwitch(3, 11, 1);
					tsi.setSpeed(this.id, myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(7);
				}
			}
			else{
				if((otherState.getState()==3)||(otherState.getState()==1)){
					tsi.setSwitch(3,11,1);
					tsi.setSwitch(4,9,0);
					tsi.setSpeed(this.id, myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(3);					
				}
				else{
					tsi.setSwitch(3,11,0);
					tsi.setSwitch(4,9,0);
					tsi.setSpeed(this.id,myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(4);				
				}
			}
			semaphore5c.release(1);

		}

		public void acquireState8(StateHolder myState, StateHolder otherState,Semaphore semaphore8c) throws InterruptedException, CommandException{
			tsi.setSpeed(this.id, 0);
			myState.changeState(8);
			semaphore8c.acquire(1);

			if((myState.getState()==6)||(myState.getState()==7)){
				if((otherState.getState()==10)||(otherState.getState()==12)){
					tsi.setSwitch(15,9,0);
					tsi.setSwitch(17,7,1);
					tsi.setSpeed(this.id, myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(9);
				}
				else{
					tsi.setSwitch(15,9,0);
					tsi.setSwitch(17,7,0);
					tsi.setSpeed(this.id,myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(10);			
				}
			}
			else{
				if((otherState.getState()==7)||(otherState.getState()==5)){
					tsi.setSwitch(17,7,0);
					tsi.setSwitch(15,9,0);
					tsi.setSpeed(this.id,myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(6);					
				}
				else{ // modified
					tsi.setSwitch(17,7,0);
					tsi.setSwitch(15,9,1);
					tsi.setSpeed(this.id,myState.getSpeed());
					SensorEvent s5c = tsi.getSensor(this.id);
					myState.changeState(7);				
				}
			}
			semaphore8c.release(1);

		}

		public void acquireState11(StateHolder myState,Semaphore semaphore11c) throws InterruptedException, CommandException {
			
			tsi.setSpeed(this.id, 0);
			semaphore11c.acquire(1);
			myState.changeState(11);
			tsi.setSpeed(this.id, myState.getSpeed());
			semaphore11c.release(1);
		}

		public void updateState(StateHolder stateMyTrain,StateHolder stateOtherTrain,int xPos,int yPos,int TrainId,TSimInterface tsi,Semaphore semaphore5c,Semaphore semaphore8c, Semaphore semaphore11c) throws InterruptedException, CommandException{

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
					acquireState5(stateMyTrain, stateOtherTrain, semaphore5c);
					break;
				}
			case 4:
				if(xPos==14){
					approachStation(stateMyTrain);
					stateMyTrain.changeState(2);
					break;
				}
				else{
					acquireState5(stateMyTrain, stateOtherTrain, semaphore5c);
					break;
				}
			case 6: 
				if(xPos==5){
					acquireState5(stateMyTrain, stateOtherTrain, semaphore5c);
					break;
				}
				else{
					acquireState8(stateMyTrain, stateOtherTrain, semaphore8c);
					break;
				}

			case 7: // modified
				//			if(xPos==4){
				if(xPos==15){
					acquireState5(stateMyTrain, stateOtherTrain, semaphore5c);
					break;
				}
				else{
					acquireState8(stateMyTrain, stateOtherTrain, semaphore8c);
					break;
				}
			case 9:
				if(xPos==17){
					acquireState8(stateMyTrain, stateOtherTrain, semaphore8c);
					break;
				}
				else{
					acquireState11(stateMyTrain, semaphore11c);
					break;
				}
			case 10:
				//			System.out.print("!!!!!!!!!!!!!!!!!!!!!!!");
				if(xPos==16){
					acquireState8(stateMyTrain, stateOtherTrain, semaphore8c);
					break;
				}
				else{
					acquireState11(stateMyTrain, semaphore11c);
					break;
				}

			case 11:
				//			System.out.print("1111111111111111111111 ui bien");
				if(xPos==9){

					stateMyTrain.changeState(10);
					//				System.out.print("bien");
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
					acquireState11(stateMyTrain, semaphore11c);
					break;
				}
			case 13:
				if(xPos==14){
					approachStation(stateMyTrain);
					stateMyTrain.changeState(15);
					break;
				}
				else{
					acquireState11(stateMyTrain, semaphore11c);

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

