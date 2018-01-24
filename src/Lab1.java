import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {

	public Lab1(Integer speed1, Integer speed2) {
		TSimInterface tsi = TSimInterface.getInstance();

		//Threads
		Semaphore semaphore5c = new Semaphore(1);
		Semaphore semaphore8c = new Semaphore(1);
		Semaphore semaphore11c = new Semaphore(1);
		new Thread(new Train(1, speed1, 14 , tsi)).start();
		new Thread(new Train(2, speed2, 1, tsi)).start();
	}

	class Train implements Runnable {
		private int id;
		private int speed;
		private int state;
		private TSimInterface tsi;

		public Train(int id, int speed, int state, TSimInterface tsi) {
			this.id = id;
			this.speed = speed;
			this.state = state;
			this.tsi = tsi;
		}

		public int getId(){
			return id;
		}

		public int getState(){ 
			return state;
		}
		public Integer getSpeed(){ 
			return speed;
		}

		public void changeSpeed(Integer value) throws CommandException{
			this.tsi.setSpeed(this.id, this.speed);
		}

		public void changeState(int value){
			this.state = value;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				//				semaphore1.acquire(1);
				System.out.print("1-IN:");
				System.out.print(this.state);

				SensorEvent s1 = tsi.getSensor(1);
				System.out.println("==========");
				if(s1.getStatus()==1){
					int myId = s1.getTrainId(); 
					updateState( int state2, s1.getXpos(), s1.getYpos(), myId, tsi);
				}
				System.out.print("1-OUT:");
				System.out.print(this.state);
			}
		}

		public void approachStation() throws InterruptedException, CommandException {
			Thread.sleep(1000);
			changeSpeed(-this.getSpeed());
		}

		public void acquireState5(int otherState) throws InterruptedException, CommandException {
			exceptSpeed(_tsi, TrainId, 0);
			semaphore5c.acquire(1);
			System.err.println("sema5, myState: " + myState.getState() + ", otherState:"+otherState.getState() + ", trainid:"+ TrainId);
			if((myState.getState()==3)||(myState.getState()==4)){
				//			if((otherState.getState()==6)||(otherState.getState()==8)){
				if(otherState.getState()==7 ||(otherState.getState()==8) ){
					exceptSwitch(_tsi,3,11,0);
					exceptSwitch(_tsi,4,9,0);
					exceptSpeed(_tsi,TrainId,myState.getSpeed());
					SensorEvent s5c = _tsi.getSensor(TrainId);
					myState.changeState(6);			
				}
				else{
					_tsi.setSwitch(4, 9, 1);
					_tsi.setSwitch(3, 11, 1);
					//				exceptSwitch(tsi,15,9,1);
					exceptSpeed(_tsi,TrainId,myState.getSpeed());
					SensorEvent s5c = _tsi.getSensor(TrainId);
					myState.changeState(7);
				}
			}
			else{
				if((otherState.getState()==3)||(otherState.getState()==1)){
					exceptSwitch(_tsi,3,11,1);
					exceptSwitch(_tsi,4,9,0);
					exceptSpeed(_tsi,TrainId,myState.getSpeed());
					SensorEvent s5c = _tsi.getSensor(TrainId);
					myState.changeState(3);					
				}
				else{
					exceptSwitch(_tsi,3,11,0);
					exceptSwitch(_tsi,4,9,0);
					exceptSpeed(_tsi,TrainId,myState.getSpeed());
					SensorEvent s5c = _tsi.getSensor(TrainId);
					myState.changeState(4);				
				}
			}
			semaphore5c.release(1);

		}

		public void acquireState8(StateHolder myState, StateHolder otherState, TSimInterface tsi, int TrainId, Semaphore semaphore8c) throws InterruptedException, CommandException{
			exceptSpeed(tsi, TrainId, 0);
			myState.changeState(8);
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
				else{ // modified
					exceptSwitch(tsi,17,7,0);
					//				exceptSwitch(tsi,4,9,0);
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

		public void updateState(StateHolder stateMyTrain,StateHolder stateOtherTrain,int xPos,int yPos,int TrainId,TSimInterface tsi) throws InterruptedException, CommandException{

			switch(stateMyTrain.getState()){
			case 1:
				stateMyTrain.changeState(3);
				break;
			case 2:
				stateMyTrain.changeState(4);
				break;		
			case 3: // modified
				if(xPos==14){
					approachStation(tsi, TrainId, stateMyTrain);
					stateMyTrain.changeState(1);
					break;
				}
				//			else if(xPos==4){
				//				break;
				//			}
				else{
					System.out.println("get into state5");
					acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
			case 4:
				if(xPos==14){
					approachStation(tsi, TrainId, stateMyTrain);
					stateMyTrain.changeState(2);
					break;
				}
				else{
					acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
			case 6: 
				if(xPos==5){
					acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
				else{
					acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}

			case 7: // modified
				//			if(xPos==4){
				if(xPos==15){
					acquireState5(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
				else{
					acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
			case 9:
				if(xPos==17){
					acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
				else{
					acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, xPos, yPos);
					break;
				}
			case 10:
				//			System.out.print("!!!!!!!!!!!!!!!!!!!!!!!");
				if(xPos==16){
					acquireState8(stateMyTrain, stateOtherTrain, tsi, TrainId);
					break;
				}
				else{
					acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, xPos, yPos);
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
					approachStation(tsi, TrainId, stateMyTrain);
					stateMyTrain.changeState(14);
					break;
				}
				else{
					acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, xPos, yPos);
					break;
				}
			case 13:
				if(xPos==14){
					approachStation(tsi, TrainId, stateMyTrain);
					stateMyTrain.changeState(15);
					break;
				}
				else{
					acquireState11(stateMyTrain, stateOtherTrain, tsi, TrainId, xPos, yPos);

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

