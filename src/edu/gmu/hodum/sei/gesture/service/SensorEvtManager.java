package edu.gmu.hodum.sei.gesture.service;

import java.util.Vector;

import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService.RecognizerState;

public class SensorEvtManager {

	final private Vector<Evt> evts = new Vector<Evt>();
	private long nextSampleTime = 0;
	private GestureRecognizerService service;
	private long startRecognizerTime;
	private long gestureRecognizeTime;
	private long eventDelay;
	private boolean triggered;

	SensorEvtManager(GestureRecognizerService service){
		this.service = service;
		triggered = false;
	}

	public synchronized void addEvt(long time, SensorEvtType type){
		if(time > nextSampleTime){

			if(service.getState() == RecognizerState.DEACTIVATED){
				//discards QUIET and CONFIGURING Evts					

				if (type == SensorEvtType.ACTIVATE){

					System.out.println("Sensor event triggered, evts.size = "+evts.size());
					//there are not two events yet
					if(evts.size()<2){
						evts.add(new Evt(time, type));
					}
					//this is the third event
					else{

						//removes values which may be too old
						int i = evts.size()-1;
						while(i>0){
							System.out.println("remove");
							if (time - evts.get(i).getTime() > startRecognizerTime){
								evts.remove(i);
							}
							i--;
						}
						evts.add(new Evt(time, type));

						//checks to make sure that there are enough sensor events to activate the recognizer
						if(evts.size() == 3){
							evts.clear();

							//activates the recognizer
							System.out.println("Recognizer triggered");
							service.updateUI("Gesture Start");
							service.setState(RecognizerState.ACTIVATED);
							service.triggerRecognizer();
							triggered = true;

						}
					}
				}
			}
			else if(service.getState() == RecognizerState.ACTIVATED){
				//while activated, wait for "quiet" period

				if(type == SensorEvtType.QUIET){
					evts.add(new Evt(time, type));
					if(evts.get(evts.size()-1).getTime() - evts.get(0).getTime() > gestureRecognizeTime){
						//stop the recognizer
						evts.clear();
						service.triggerRecognizer();
						triggered = false;

						//catches "gesture not recognized and many "simple" gestures that do not require additional user input
						//service.setState(RecognizerState.DEACTIVATED);	
						

						//updateUI("Recognizer Stopped");
					}
				}
				//start over if there is not a quiet event
				else{
					evts.clear();
				}
			}
			else if (service.getState() == RecognizerState.CHOICE_MODE){
				//while in choice mode, wait for a non-"quiet" event to start the recognizer
				//discard the "quiet" events while the recognizer is not started
				
				if(type != SensorEvtType.QUIET && triggered == false){
					System.out.println("Choice Mode Triggered");
					service.triggerRecognizer();
					triggered = true;
				}
				else if(type == SensorEvtType.QUIET && triggered == true){
					
					//same code as the "activated" portion
					if(evts.get(evts.size()-1).getTime() - evts.get(0).getTime() > gestureRecognizeTime){
						System.out.println("Choice Mode Stopped");
						
						//stop the recognizer
						evts.clear();
						service.triggerRecognizer();
						triggered = false;

					}
					

				}
				
				
				
			}

			nextSampleTime = time + eventDelay;
		}//sample time

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
}



