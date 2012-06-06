package edu.gmu.hodum.sei.gesture.service;

interface GestureChoice {
	
	public void goNext();
	
	public void goBack();
	
	public String getCurrentUIString();
	
	public String getCurrentVal();
	
	public boolean isFinished();
	
	public void onConfirm();

}
