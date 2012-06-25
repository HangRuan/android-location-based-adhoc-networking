package edu.gmu.hodum.sei.gesture.service;

import java.util.Vector;

public class SensorEvtManager extends Thread{

	final private Vector<Evt> evts = new Vector<Evt>();
	private long nextSampleTime = 0;
	private GestureRecognizerService service;
	private long startRecognizerTime;
	private long gestureRecognizeTime;
	private long eventDelay;
	private long commandRecognizedDelay = 3000;
	private boolean isCommandRecognized;

	SensorEvtManager(GestureRecognizerService service){
		this.service = service;
		//triggered = false;
	}

	public synchronized void addEvt(long time, SensorEvtType type){
		if (service.evtLock.tryLock()){
			isCommandRecognized = false;

			if(time > nextSampleTime){

				if(GestureRecognizerService.getState() == RecognizerState.MAIN_DEACTIVATED || 
						GestureRecognizerService.getState() == RecognizerState.LEARNING_MAIN_DEACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.TEST_MAIN_DEACTIVATED ){
					
					//if deactivated, check for activate sensor events
					if (type == SensorEvtType.ACTIVATE){

						System.out.println("Sensor event triggered, evts.size = "+evts.size());
						evts.add(new Evt(time, type));

						//this is the third event
						if (evts.size() >= 3) {
							//removes values which are too old
							int i = evts.size()-1;
							while(i>0){
								if (time - evts.get(i).getTime() > startRecognizerTime){
									evts.remove(i);
								}
								i--;
							}

							//checks to make sure that there are still enough sensor events to activate the recognizer
							if(evts.size() == 3){
								evts.clear();

								//activates the recognizer
								System.out.println("Recognizer triggered");
								service.updateUI("Gesture Start");
								/*
								if(GestureRecognizerService.getState() == RecognizerState.MAIN_DEACTIVATED){
									GestureRecognizerService.setState(RecognizerState.MAIN_ACTIVATED);
								}
								else if (GestureRecognizerService.getState() == RecognizerState.LEARNING_MAIN_DEACTIVATED){
									GestureRecognizerService.setState(RecognizerState.LEARNING_MAIN_ACTIVATED);
								}
								else if (GestureRecognizerService.getState() == RecognizerState.TEST_MAIN_DEACTIVATED){
									GestureRecognizerService.setState(RecognizerState.TEST_MAIN_ACTIVATED);
								}*/
								service.triggerRecognizer();
								//triggered = true;
							}
						}
					}
				}
				else if(GestureRecognizerService.getState() == RecognizerState.MAIN_ACTIVATED || 
						GestureRecognizerService.getState() == RecognizerState.LEARNING_MAIN_ACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.TEST_MAIN_ACTIVATED){
					//while activated, wait for "quiet" period
					stopRecognizerOnQuiet(time, type);
				}
				else if(GestureRecognizerService.getState() == RecognizerState.COMPASS_MODE){

					//Reads the compass
					if (type == SensorEvtType.ACTIVATE){
						service.triggerCompassRead();
						isCommandRecognized = true;
					}
				}
				else if(GestureRecognizerService.getState() == RecognizerState.CHOICE_DEACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.LEARNING_CHOICE_DEACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.TEST_CHOICE_DEACTIVATED){

					//recognizer is triggered
					if (type == SensorEvtType.ACTIVATE){
						service.updateUI("Gesture Start");
						service.triggerRecognizer();
						
						
						/*
						if(GestureRecognizerService.getState() == RecognizerState.CHOICE_DEACTIVATED){
							GestureRecognizerService.setState(RecognizerState.CHOICE_ACTIVATED);
						}
						else if (GestureRecognizerService.getState() == RecognizerState.LEARNING_CHOICE_DEACTIVATED) {
							GestureRecognizerService.setState(RecognizerState.LEARNING_CHOICE_ACTIVATED);
						}
						else if (GestureRecognizerService.getState() == RecognizerState.TEST_CHOICE_DEACTIVATED) {
							GestureRecognizerService.setState(RecognizerState.TEST_CHOICE_ACTIVATED);
						}
						*/
						
					}

				}
				else if(GestureRecognizerService.getState() == RecognizerState.CHOICE_ACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.LEARNING_CHOICE_ACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.TEST_CHOICE_ACTIVATED){
					stopRecognizerOnQuiet(time, type);
				}
				


				//calculates the next sample time, dependent on whether a command was recognized
				if(isCommandRecognized){
					nextSampleTime = time + eventDelay + commandRecognizedDelay;
				}
				else {
					nextSampleTime = time + eventDelay;
				}
			}	//sample time
			service.evtLock.unlock();
		}

	}

	public void setEventDelay(long time){
		eventDelay = time;
	}
	public void setGestureRecognizeTime(long time){
		gestureRecognizeTime = time;
	}	
	public void setStartRecognizerTime(long time){
		startRecognizerTime = time;
	}

	private void stopRecognizerOnQuiet(long time, SensorEvtType type){
		if(type == SensorEvtType.QUIET){
			evts.add(new Evt(time, type));
			//if there is an extended period of "quiet" 
			if(evts.get(evts.size()-1).getTime() - evts.get(0).getTime() > gestureRecognizeTime){

				evts.clear();

				//catches "gesture not recognized" and many "simple" gestures that do not require additional user input
				//GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);

				//stop the recognizer
				service.triggerRecognizer();
				

				isCommandRecognized = true;

			}
		}
		else{
			evts.clear();
		}

	}


}



