import TSim.*;
import java.util.concurrent.Semaphore;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

public class Lab1 {
	private TSimInterface tsi;
	private Semaphore semaphore5c;
	private Semaphore semaphore8c;
	private Semaphore semaphore11c;
	private Semaphore semaphore34;
	private Semaphore semaphore67;
	private Semaphore semaphore1415;
	private Dictionary<Integer, int[]> sensors;
	State state1;
	State state2;

	public Lab1(Integer speed1, Integer speed2) {
		tsi = TSimInterface.getInstance();

		semaphore5c = new Semaphore(1);
		semaphore8c = new Semaphore(1);
		semaphore11c = new Semaphore(1);
		semaphore34 = new Semaphore(1);
		semaphore67 = new Semaphore(1);
		semaphore1415 = new Semaphore(1);
		sensors = new Hashtable<Integer, int[]>();
		this.addSensors();

		state1 = new State(12);
		state2 = new State(3);
		new Thread(new Train(1, speed1, true)).start();
		new Thread(new Train(2, speed2, false)).start();
	}

	public void addSensors(){
		sensors.put(0, new int[]{12,11});
		sensors.put(1, new int[]{12,13});
		sensors.put(2, new int[]{5,11});
		sensors.put(3, new int[]{3,13});
		sensors.put(4, new int[]{7,9});
		sensors.put(5, new int[]{6,10});
		sensors.put(6, new int[]{12,9});
		sensors.put(7, new int[]{13,10});
		sensors.put(8, new int[]{15,8});
		sensors.put(9, new int[]{14,7});
		sensors.put(10, new int[]{10,8});
		sensors.put(11, new int[]{10,7});
		sensors.put(12, new int[]{6,7});
		sensors.put(13, new int[]{8,5});
		sensors.put(14, new int[]{13,5});
		sensors.put(15, new int[]{12,3});
		sensors.put(16, new int[]{19,8});
		sensors.put(17, new int[]{1,10});
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
		}
	}

	class Train implements Runnable {
		private int id;
		private int speed;
		private boolean direction;

		public Train(int id,int speed, boolean direction) {
			this.id = id;
			this.speed = speed;
			this.direction = direction;
		}

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
					SensorEvent s;		
					s = tsi.getSensor(this.id);
					if(s.getStatus()==1){ 
						updateState(chooseState(), chooseOtherState(), s.getXpos(), s.getYpos(), id);
					}
				}
			} catch (CommandException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void approachStation(State state) throws InterruptedException, CommandException {
			Thread.sleep(500);
			tsi.setSpeed(this.id, 0);
			Thread.sleep(1000);
			this.speed = -this.speed;
			this.direction = !this.direction;
			tsi.setSpeed(this.id, this.speed);
		}

		public void updateState(State stateMyTrain,State stateOtherTrain,int xPos,int yPos,int TrainId) throws InterruptedException, CommandException{
			int[] currSensor = new int[]{xPos, yPos}; 
			// based on current state, and current activated sensor
			System.out.println("state:" + stateMyTrain.getState()+" trainid:" + this.id);
			switch(stateMyTrain.getState()){
			case 3: 
				if( Arrays.equals(currSensor, sensors.get(0)) && this.direction){ // 12,11
					semaphore5c.release();
					System.out.println("current state:" + stateMyTrain.getState());
					approachStation(stateMyTrain);
				}
				else if(Arrays.equals(currSensor, sensors.get(2)) && !this.direction){ // 5,11
					tsi.setSpeed(this.id, 0);
					semaphore5c.acquire();
					tsi.setSwitch(3, 11, 1); 
					tsi.setSpeed(this.id, this.speed);
					stateMyTrain.changeState(5);
				}	break;
			case 4:
				if( Arrays.equals(currSensor, sensors.get(1)) && this.direction){ // 12,13
					semaphore5c.release();
					approachStation(stateMyTrain);
				}
				else if(Arrays.equals(currSensor, sensors.get(3)) && !this.direction){ // 3,13
					tsi.setSpeed(this.id, 0);
					semaphore5c.acquire();
					tsi.setSwitch(3, 11, 0);  
					tsi.setSpeed(this.id, this.speed);
					stateMyTrain.changeState(5);
				}	break;
			case 5:
				tsi.setSpeed(this.id, 0);
				if(this.direction){ // train runs from up to down
					if(semaphore67.availablePermits() == 0){
						semaphore67.release();
					}
					if (semaphore34.availablePermits() == 0){
						stateMyTrain.changeState(4);
						tsi.setSwitch(3, 11, 0);
					}
					else{
						semaphore34.acquire();
						stateMyTrain.changeState(3);
						tsi.setSwitch(3, 11, 1); 
					}
				}
				else{
					if(semaphore34.availablePermits() == 0){
						semaphore34.release();
					}
					if(semaphore67.availablePermits() == 0){
						stateMyTrain.changeState(6);
						tsi.setSwitch(4, 9, 1);
					}
					else{
						semaphore67.acquire();
						stateMyTrain.changeState(7);
						tsi.setSwitch(4, 9, 0);
					}
				}
				//				semaphore5c.release(); release after enter 67 or 34 
				tsi.setSpeed(this.id, this.speed);
				break;
			case 6: 
				if(Arrays.equals(currSensor, sensors.get(4))){ // 6,9
					if(this.direction){
						semaphore8c.release();
						tsi.setSpeed(this.id, 0);
						semaphore5c.acquire();
						tsi.setSwitch(4, 9, 1); 
						stateMyTrain.changeState(5);
					}
				}
				else{ // 13,9
					if(!this.direction){
						tsi.setSpeed(this.id, 0);
						semaphore5c.release();
						semaphore8c.acquire();
						tsi.setSwitch(15, 9, 0); 
						stateMyTrain.changeState(8);
					}
				}
				tsi.setSpeed(this.id, this.speed);
				break;

			case 7:
				if(Arrays.equals(currSensor, sensors.get(5))){ // 6,10
					if(this.direction){
						semaphore8c.release();
						tsi.setSpeed(this.id, 0);
						semaphore5c.acquire();
						tsi.setSwitch(4, 9, 0);
						stateMyTrain.changeState(5);
					}
				}
				else{ // 15,10
					if(!this.direction){
						tsi.setSpeed(this.id, 0);
						System.out.println("semaphore8c" + semaphore8c.availablePermits());
						semaphore5c.release();
						semaphore8c.acquire();
						System.out.println("semaphore8c" + semaphore8c.availablePermits());
						tsi.setSwitch(15, 9, 1); 
						stateMyTrain.changeState(8);
					}
				}//				semaphore67.release(); release 67 when state 8 
				tsi.setSpeed(this.id, this.speed);
				break;

			case 8: 
				tsi.setSpeed(this.id, 0);
				if(this.direction ){
					if (semaphore1415.availablePermits() == 0) {
						semaphore1415.release();
					}
					if(semaphore67.availablePermits() == 0) {
						stateMyTrain.changeState(6);
						tsi.setSwitch(15, 9, 0);
					}
					else{

						semaphore67.acquire();
						stateMyTrain.changeState(7);
						tsi.setSwitch(15, 9, 1);
					}
				}
				else{ // from down to up
					if(semaphore67.availablePermits() == 0) {
						semaphore67.release();
					}
					if(semaphore1415.availablePermits() == 0){
						stateMyTrain.changeState(10);
						tsi.setSwitch(17, 7, 0);
					}
					else{
						semaphore1415.acquire();
						stateMyTrain.changeState(9);
						tsi.setSwitch(17, 7, 1); 
					}
				}//				semaphore8c.release(); after enter state 9 or 10 
				tsi.setSpeed(this.id, this.speed);
				break;
			case 9:
				tsi.setSpeed(this.id, 0);
				if( Arrays.equals(currSensor, sensors.get(8)) && this.direction){ // 15,8
					semaphore11c.release();
					semaphore8c.acquire();
					tsi.setSwitch(17, 7, 1);
					stateMyTrain.changeState(8);
				}
				else if (Arrays.equals(currSensor, sensors.get(10)) && !this.direction){ // 10,8					
					semaphore8c.release();
					semaphore11c.acquire();
					stateMyTrain.changeState(11);
				}
				tsi.setSpeed(this.id, this.speed);
				break;
			case 10:				
				tsi.setSpeed(this.id, 0);
				if( Arrays.equals(currSensor, sensors.get(9)) && this.direction){ // 15,7
					semaphore11c.release();
					semaphore8c.acquire();
					tsi.setSwitch(17, 7, 0);
					stateMyTrain.changeState(8);	
				}
				else if(Arrays.equals(currSensor, sensors.get(11)) && !this.direction){ // 10,7
					semaphore8c.release();
					semaphore11c.acquire();
					stateMyTrain.changeState(11);
				}
				tsi.setSpeed(this.id, this.speed);
				break;

			case 11:
				tsi.setSpeed(this.id, 0);
				if(Arrays.equals(currSensor, sensors.get(11)) && this.direction){ // 10,7
					stateMyTrain.changeState(10);
				}
				else if( Arrays.equals(currSensor, sensors.get(12)) && !this.direction){ // 6,7
					stateMyTrain.changeState(12);
				}
				else if( Arrays.equals(currSensor, sensors.get(10)) && this.direction){ // 10,8
					stateMyTrain.changeState(9);
				}
				else{
					if(!this.direction){
						stateMyTrain.changeState(13);
					}
				}  	 
				tsi.setSpeed(this.id, this.speed);
				break;

			case 12: 
				if( Arrays.equals(currSensor, sensors.get(15)) && !this.direction){ // 12,3
					semaphore11c.release();
					approachStation(stateMyTrain);
				}
				else{ // 6,7
					tsi.setSpeed(this.id, 0);
					semaphore11c.acquire();
					stateMyTrain.changeState(11);
					tsi.setSpeed(this.id, this.speed);
				}	break;
			case 13:
				if( Arrays.equals(currSensor, sensors.get(14)) && !this.direction){ // 13,5
					semaphore11c.release();
					approachStation(stateMyTrain);
				}
				else{
					tsi.setSpeed(this.id, 0);
					semaphore11c.acquire();
					stateMyTrain.changeState(11);
					tsi.setSpeed(this.id, this.speed);
				}
				break;
			default:
				break;
			}
		}
	}
}