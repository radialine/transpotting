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
	private Semaphore semaphore910;
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
		semaphore910 = new Semaphore(1);
		sensors = new Hashtable<Integer, int[]>();
		this.addSensors();

		state1 = new State(12);
		state2 = new State(3);
		
		new Thread(new Train(1, speed1, true, new boolean[]{false, false, false, false, false, false})).start();
		new Thread(new Train(2, speed2, false, new boolean[]{false,false, false, false, false, false})).start();
	}

	public void addSensors(){
		sensors.put(0, new int[]{12,11});
		sensors.put(1, new int[]{12,13});
		sensors.put(2, new int[]{6,11});
		sensors.put(3, new int[]{5,13});
		sensors.put(4, new int[]{7,9});
		sensors.put(5, new int[]{6,10});
		sensors.put(6, new int[]{12,9});
		sensors.put(7, new int[]{13,10});
		sensors.put(8, new int[]{15,8});
		sensors.put(9, new int[]{14,7});
		sensors.put(10, new int[]{11,8});
		sensors.put(11, new int[]{11,7});
		sensors.put(12, new int[]{6,6});
		sensors.put(13, new int[]{9,5});
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
		// represent semaphores that current train is holding. 
		private boolean[] semaphoreHolding ;

		public Train(int id,int speed, boolean direction, boolean[]semaphores) {
			this.id = id;
			this.speed = speed;
			this.direction = direction;
			this.semaphoreHolding = semaphores;
		}

		public State chooseState(){
			if (this.id==1){ return state1; }
			else{ return state2; }
		}

		@Override
		public void run() {
			try {
				tsi.setSpeed(this.id, this.speed);
				while(true){
					SensorEvent s;
					s = tsi.getSensor(this.id);
					if(s.getStatus()==1){ 
						updateState(chooseState(), s.getXpos(), s.getYpos(), id);
					}
				}
			} catch (CommandException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void approachStation(State state) throws InterruptedException, CommandException {
			tsi.setSpeed(this.id, 0);
			Thread.sleep(1000);
			this.speed = -this.speed;
			this.direction = !this.direction;
			tsi.setSpeed(this.id, this.speed);
		}

		public void updateState(State stateMyTrain,int xPos,int yPos,int TrainId) throws InterruptedException, CommandException{
			int[] currSensor = new int[]{xPos, yPos}; 
			// based on current state, and current activated sensor
			System.out.println("state:" + stateMyTrain.getState()+" trainid:" + this.id);
			switch(stateMyTrain.getState()){
			case 3: 
				if( Arrays.equals(currSensor, sensors.get(0)) && this.direction){ // 12,11
					approachStation(stateMyTrain);
				}
				else if(Arrays.equals(currSensor, sensors.get(2))){ // 5,11
					System.out.println(this.id+","+this.direction + ","+semaphore34.availablePermits()+","+
				semaphore5c.availablePermits());
					if (this.direction){	semaphore5c.release(); this.semaphoreHolding[3] = false;	}
					else {
						tsi.setSpeed(this.id, 0);
						semaphore5c.acquire();
						this.semaphoreHolding[3] = true;
						tsi.setSwitch(3, 11, 1); 
						tsi.setSpeed(this.id, this.speed);
						stateMyTrain.changeState(5);
					}
				}	break;
			case 4:
//				System.out.println(this.id+","+this.direction + ","+semaphore34.availablePermits()+","+
//			semaphore5c.availablePermits());
				System.out.println("in state 4:"+semaphore67.availablePermits() + "," + this.semaphoreHolding.toString());
				if( Arrays.equals(currSensor, sensors.get(1)) && this.direction){ // 12,13
					if(semaphore34.availablePermits() != 0){
						semaphore34.acquire();
						this.semaphoreHolding[0] = true;
					}
					approachStation(stateMyTrain);
				}
				else if(Arrays.equals(currSensor, sensors.get(3))){ // 5,13
					if(this.direction) {	semaphore5c.release();	this.semaphoreHolding[3] = false;}
					else {
						tsi.setSpeed(this.id, 0);
						semaphore5c.acquire();
						this.semaphoreHolding[3] = true;
						tsi.setSwitch(3, 11, 0); 
						tsi.setSpeed(this.id, this.speed);
						stateMyTrain.changeState(5);
					}
				}	break;
			case 5:
				System.out.println("in state 5:"+semaphore67.availablePermits()+ "," + this.semaphoreHolding.toString());
				tsi.setSpeed(this.id, 0);
				if(this.direction){ // train runs from up to down
					if(semaphore67.availablePermits() == 0 && this.semaphoreHolding[1]){
						semaphore67.release();
						this.semaphoreHolding[1] = false;
					}
					if (semaphore34.availablePermits() == 0){
						System.out.println("!!!!!!!!!!!");
						stateMyTrain.changeState(3);
						tsi.setSwitch(3, 11, 1);
					}
					else{
						
						semaphore34.acquire();
						this.semaphoreHolding[0] = true;
						stateMyTrain.changeState(4);
						tsi.setSwitch(3, 11, 0);
					}
				}
				else{
					if(semaphore34.availablePermits() == 0 && this.semaphoreHolding[0]){
						semaphore34.release();
						this.semaphoreHolding[0]=false;
					}
					if(semaphore67.availablePermits() == 0){
						stateMyTrain.changeState(6);
						tsi.setSwitch(4, 9, 1);
					}
					else{
						semaphore67.acquire();
						this.semaphoreHolding[1] = true;
						stateMyTrain.changeState(7);
						tsi.setSwitch(4, 9, 0);
					}
				} 
				tsi.setSpeed(this.id, this.speed);
				break;
			case 6: 
				if(Arrays.equals(currSensor, sensors.get(4))){ // 7,9
					if(this.direction){
						tsi.setSpeed(this.id, 0);
						semaphore5c.acquire();
						this.semaphoreHolding[3] = true;
						tsi.setSwitch(4, 9, 1); 
						stateMyTrain.changeState(5);
					}
					else{	semaphore5c.release(); 	this.semaphoreHolding[3] = false;}
				}
				else{ // 13,9
					if(!this.direction){
						tsi.setSpeed(this.id, 0);
						semaphore8c.acquire();
						this.semaphoreHolding[4] = true;
						tsi.setSwitch(15, 9, 0); 
						stateMyTrain.changeState(8);
					}
					else {	semaphore8c.release();	this.semaphoreHolding[4] = false;}
				}
				tsi.setSpeed(this.id, this.speed);
				break;

			case 7:
				System.out.println("in state 7:" + semaphore67.availablePermits());
				if(Arrays.equals(currSensor, sensors.get(5))){ // 6,10
					if(this.direction){
						tsi.setSpeed(this.id, 0);
						semaphore5c.acquire();
						this.semaphoreHolding[3] = true;
						tsi.setSwitch(4, 9, 0);
						stateMyTrain.changeState(5);
					}
					else{	semaphore5c.release(); 	this.semaphoreHolding[3] = false;}
				}
				else{ // 15,10
					if(!this.direction){
						tsi.setSpeed(this.id, 0);
						semaphore8c.acquire();
						this.semaphoreHolding[4] = true;
						tsi.setSwitch(15, 9, 1); 
						stateMyTrain.changeState(8);
					}
					else {	semaphore8c.release();	this.semaphoreHolding[4] = false;}
				} 
				tsi.setSpeed(this.id, this.speed);
				break;

			case 8: 
				System.out.println("in state 9:"+semaphore910.availablePermits());
				tsi.setSpeed(this.id, 0);
				if(this.direction ){
					if (semaphore910.availablePermits() == 0 && this.semaphoreHolding[2]) {
						semaphore910.release();
						this.semaphoreHolding[2] = false;
					}
					if(semaphore67.availablePermits() == 0) {
						stateMyTrain.changeState(6);
						tsi.setSwitch(15, 9, 0);
					}
					else{
						semaphore67.acquire();
						this.semaphoreHolding[1] = true;
						stateMyTrain.changeState(7);
						tsi.setSwitch(15, 9, 1);
					}
				}
				else{ // from down to up
					if(semaphore67.availablePermits() == 0 && this.semaphoreHolding[1]) {
						semaphore67.release();
						this.semaphoreHolding[1] = false;
					}
					if(semaphore910.availablePermits() == 0){
						stateMyTrain.changeState(10);
						tsi.setSwitch(17, 7, 0);
					}
					else{
						semaphore910.acquire();
						this.semaphoreHolding[2] = true;
						stateMyTrain.changeState(9);
						tsi.setSwitch(17, 7, 1); 
					}
				} 
				tsi.setSpeed(this.id, this.speed);
				break;
			case 9:
				System.out.println("in state 9:"+semaphore910.availablePermits());
				tsi.setSpeed(this.id, 0);
				if( Arrays.equals(currSensor, sensors.get(8))){ // 15,8
					if(this.direction){
						semaphore8c.acquire();
						this.semaphoreHolding[4] = true;
						tsi.setSwitch(17, 7, 1);
						stateMyTrain.changeState(8);
					}
					else {	semaphore8c.release();	this.semaphoreHolding[4] = false;}
				}
				else if (Arrays.equals(currSensor, sensors.get(10)) && !this.direction){ // 11,8
					semaphore11c.acquire();
					this.semaphoreHolding[5] = true;
					stateMyTrain.changeState(11);
				}
				tsi.setSpeed(this.id, this.speed);
				break;
			case 10:				
				System.out.println("in state 10:"+semaphore910.availablePermits());
				tsi.setSpeed(this.id, 0);
				if( Arrays.equals(currSensor, sensors.get(9))){ // 15,7
					if(this.direction){
						semaphore8c.acquire();
						this.semaphoreHolding[4] = true;
						tsi.setSwitch(17, 7, 0);
						stateMyTrain.changeState(8);
					}
					else {	semaphore8c.release(); this.semaphoreHolding[4] = false;}
				}
				else if(Arrays.equals(currSensor, sensors.get(11)) && !this.direction){ // 11,7
					semaphore11c.acquire();
					this.semaphoreHolding[5] = true;
					stateMyTrain.changeState(11);
				}
				tsi.setSpeed(this.id, this.speed);
				break;

			case 11:
				System.out.println("in state 11:"+semaphore910.availablePermits());
				tsi.setSpeed(this.id, 0);
				if(Arrays.equals(currSensor, sensors.get(11)) && this.direction){ // 11,7
					System.out.println("11,7");
					System.out.println(this.id+","+stateMyTrain+",semaphore11:"+semaphore11c.availablePermits());
					stateMyTrain.changeState(10);
				}
				else if( Arrays.equals(currSensor, sensors.get(12)) && !this.direction){ // 6,7
					System.out.println("6,7");
					System.out.println(this.id+","+stateMyTrain+",semaphore11:"+semaphore11c.availablePermits());
					stateMyTrain.changeState(12);
				}
				else if( Arrays.equals(currSensor, sensors.get(10)) && this.direction){ // 11,8
					System.out.println("11,8");
					System.out.println(this.id+","+stateMyTrain+",semaphore11:"+semaphore11c.availablePermits());
					stateMyTrain.changeState(9);
				}
				else{
					if(!this.direction){
						System.out.println(this.id+","+stateMyTrain+",semaphore11:"+semaphore11c.availablePermits());
						stateMyTrain.changeState(13);
					}
				}
				semaphore11c.release();
				this.semaphoreHolding[5] = false;
				tsi.setSpeed(this.id, this.speed);
				break;

			case 12: 
				System.out.println("in state 12:"+semaphore910.availablePermits());
				if( Arrays.equals(currSensor, sensors.get(15)) && !this.direction){ // 12,3
					approachStation(stateMyTrain);
				}
				else if(Arrays.equals(currSensor, sensors.get(12))){ // 6,7, direction must be true
					tsi.setSpeed(this.id, 0);
					semaphore11c.acquire();
					this.semaphoreHolding[5] = true;
					stateMyTrain.changeState(11);
					tsi.setSpeed(this.id, this.speed);
				}	break;
			case 13:
				System.out.println("in state 13:"+semaphore910.availablePermits());
				if( Arrays.equals(currSensor, sensors.get(14)) && !this.direction){ // 13,5
					if(semaphore910.availablePermits() != 0 ){
						semaphore910.acquire();
						this.semaphoreHolding[2] = true;
					}
					approachStation(stateMyTrain);
				}
				else if(Arrays.equals(currSensor, sensors.get(13))){
					tsi.setSpeed(this.id, 0);
					semaphore11c.acquire();
					this.semaphoreHolding[5] = true;
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