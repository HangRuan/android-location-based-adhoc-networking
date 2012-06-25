package edu.gmu.hodum.sei.gesture.service;

public class MetricDistanceChoice implements GestureChoice{

	private String[] vals = new String[]{"1 meter", "2 meters", "5 meters", "10 meters", "20 meters", "50 meters", "100 meters", "200 meters", "500 meters"};
	private int index = 0;
	private int num = 1;

	private boolean finished= false; 

	MetricDistanceChoice(){

	}

	@Override
	public void goNext() {

		if(index <vals.length-1){
			index++;
		}	

	}

	@Override
	public void goBack() {

		if(index >0){
			index--;
		}

	}

	@Override
	public String getCurrentUIString() {
		return vals[index];
	}

	public boolean isFinished(){
		return finished;
	}

	public void onConfirm(){
		finished = true;

	}

	@Override
	public String getCurrentVal() {
		int val = num;

		switch (index){
		case 0: val = 1;
		case 1: val = 2;
		break;
		case 2: val = 5;
		break;
		case 3: val = 10;
		break;
		case 4: val = 20;
		break;
		case 5: val = 50;
		break;
		case 6: val = 100;
		break;
		case 7: val = 200;
		break;
		case 8: val = 500;
		break;
		default: val = 0;
		break;
		}
		return String.valueOf(val);
	}
}
