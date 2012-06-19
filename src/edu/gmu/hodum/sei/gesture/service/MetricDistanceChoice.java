package edu.gmu.hodum.sei.gesture.service;

public class MetricDistanceChoice implements GestureChoice{
	
	private String[] vals = new String[]{"meters", "ten meters", "hundred meters", "kilometers"};
	private int index = 0;
	private int num = 1;
	
	private boolean finished= false; 
	private Mode mode = Mode.UNITS;
	
	enum Mode{
		UNITS,
		SIG_DIGIT
	}

	MetricDistanceChoice(){
		
	}
	
	@Override
	public void goNext() {
		if(mode == Mode.UNITS){
			if(index <vals.length-1){
				index++;
			}	
		}
		else{
			if(num<9){
				num++;
			}
		}
	}

	@Override
	public void goBack() {
		if(mode == Mode.UNITS){
			if(index >0){
				index--;
			}
		}
		else{
			if(num>2){
				num--;
			}
		}
	}

	@Override
	public String getCurrentUIString() {
		System.out.println("Metric Distance Choice getCurrentUIString");
		if(mode == Mode.UNITS){
			System.out.println("Metric Distance Choice UNITS: "+vals[index]);
			return vals[index];
		}
		else {
			if(num == 1 && index == 0){
				System.out.println("Metric Distance Choice UNITS: 1 meter");
				return "1 meter";
			}
			System.out.println("Metric Distance Choice UNITS: "+num+" "+vals[index]);
			return num + " " + vals[index];
		}
	}
	
	public boolean isFinished(){
		return finished;
	}
	
	public void onConfirm(){
		if (mode == Mode.UNITS){
			mode = Mode.SIG_DIGIT;
		}
		//mode is currently the significant digit
		else {
			finished = true;
		}
	}

	@Override
	public String getCurrentVal() {
		int val = num;
		
		if (index == 1){
			val = val * 10;
		}
		else if(index == 2){
			val = val * 100;
		}
		else if(index == 3){
			val = val * 1000;
		}
		return String.valueOf(val);
	}
}
