package edu.gmu.hodum.sei.gesture.service;

import java.util.Vector;

import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService.RecognizerState;

public class SensorEvtManager extends Thread{

	final private Vector<Evt> evts = new Vector<Evt>();
	private long nextSampleTime = 0;
	private GestureRecognizerService service;
	private long startRecognizerTime;
	private long gestureRecognizeTime;
	private long eventDelay;
	private boolean triggered;
	private long commandRecognizedDelay = 5000;
	private boolean isCommandRecognized;

	SensorEvtManager(GestureRecognizerService service){
		this.service = service;
		//triggered = false;
	}

	public synchronized void addEvt(long time, SensorEvtType type){
		if (service.evtLock.tryLock()){
			isCommandRecognized = false;
			
			if(time > nextSampleTime){

				if(service.getState() == RecognizerState.DEACTIVATED){
					if (type == SensorEvtType.ACTIVATE){

						System.out.println("Sensor event triggered, evts.size = "+evts.size());
						evts.add(new Evt(time, type));

						//this is the third event
						if (evts.size() >= 3) {
							//removes values which may be too old
							int i = evts.size()-1;
							while(i>0){
								if (time - evts.get(i).getTime() > startRecognizerTime){
									evts.remove(i);
								}
								i--;
							}

							//checks to make sure that there are still enough sensor events to activate the recognizer
							if(evts.size() >= 3){
								evts.clear();

								//activates the recognizer
								System.out.println("Recognizer triggered");
								service.updateUI("Gesture Start");
								service.setState(RecognizerState.ACTIVATED);
								service.triggerRecognizer();
								//triggered = true;
							}
						}
					}
				}
				else if(service.getState() == RecognizerState.ACTIVATED){
					//while activated, wait for "quiet" period

					if(type == SensorEvtType.QUIET){
						stopRecognizerOnQuiet(time, type);
					}
					//reset the events list if there is not a quiet event
					else{
						evts.clear();
					}
				}
				else if(service.getState() == RecognizerState.COMPASS_MODE){

					if (type == SensorEvtType.ACTIVATE){
						service.triggerCompassRead();
						isCommandRecognized = true;
					}

				}
				else if(service.getState() == RecognizerState.CHOICE_MODE){

					if(triggered == false){
						if (type == SensorEvtType.ACTIVATE){
							service.triggerRecognizer();
						}
					}
					//recognizer is triggered
					else{
						stopRecognizerOnQuiet(time, type);
					}

				}

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
		evts.add(new Evt(time, type));
		//if there is an extended period of "quiet" 
		if(evts.get(evts.size()-1).getTime() - evts.get(0).getTime() > gestureRecognizeTime){
			
			evts.clear();
			
			//stop the recognizer
			//catches "gesture not recognized and many "simple" gestures that do not require additional user input
			service.setState(RecognizerState.DEACTIVATED);
			
			service.triggerRecognizer();
			triggered = false;

			isCommandRecognized = true;
				
		}
	}


}



