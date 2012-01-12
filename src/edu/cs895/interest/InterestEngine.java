package edu.cs895.interest;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import edu.cs895.database.EventDBAdapter;
import edu.cs895.message.Event;
import edu.cs895.LocationHolder;
import edu.cs895.UIDisplay;
import edu.cs895.message.Coder;
import edu.cs895.message.EventType;
import edu.cs895.message.MessageBuffer;
import edu.cs895.message.TransferQueue;



public class InterestEngine  {
	private static InterestEngine INSTANCE = null;
	private TransferQueue transferQ;
	private AreaOfRelevance aor;
	private boolean isRunning;
	private final String TAG = "INTEREST ENGINE";
	private UIDisplay uiDisplay;
	private EventDBAdapter dbHelper;
	private Vector<InterestReceiver> receivers = new Vector<InterestReceiver>();
	
	private InterestEngine() {}
	
	public static InterestEngine getInstance() {
		if (INSTANCE == null){
			INSTANCE = new InterestEngine();
			INSTANCE.isRunning = false;
		}
		return INSTANCE;
	}
	
	public void setUIDisplay(UIDisplay uiDisplay) {
		this.uiDisplay = uiDisplay;
	}
	
	
	public boolean registerInterest(InterestReceiver receiver)
	{
		return receivers.add(receiver);
	}
	
	public boolean unregisterInterest(InterestReceiver receiver)
	{
		return receivers.remove(receiver);
	}
	
	private void notifyReceivers(Event evt)
	{
		for(InterestReceiver rcvrs:receivers)
		{
			rcvrs.receivePacket(evt);
		}
	}
	
	public void shuttingDown()
	{
		isRunning = false;
		transferQ = null;
		dbHelper.close();
	}
	
	public void sendEvent(Event event) {
		Log.d(TAG, "sending message: " + event.getMsgId());
		MessageBuffer msgBuff = new MessageBuffer();
		msgBuff.setTargetLoc(event.getTargetLoc());
		msgBuff.setOrigLoc(event.getOrigLoc());
		msgBuff.setBuffer(Coder.encodeEvent(event));
		transferQ.toNetworkMsg(msgBuff);
		dbHelper.createEvent(event.getMsgId(), event.getEventType().getValueOf(), event.getTimestamp().getTime(),
				event.getTargetLoc().getLatitude(), event.getTargetLoc().getLongitude(),
				event.getOrigLoc().getLatitude(), event.getOrigLoc().getLongitude());
		uiDisplay.displayMessage("Sent Message: " + event.getMsgId(), -1.0);
		
		//HACK to test code!  :)
//		transferQ.toAppMsg(msgBuff);
	}
	
	public void init(TransferQueue transferQ, LocationHolder locHolder, Context context) {
		Log.d(TAG, "Entering init for Interest Engine.");
		if (isRunning) {
			return;
		}
		
		dbHelper = new EventDBAdapter(context);
		dbHelper.open();
		this.aor = AreaOfRelevance.getInstance(locHolder);
		this.transferQ = transferQ;
		
		isRunning = true;
		new Thread(new Runnable (){
			@Override
			public void run() {
				Log.d(TAG, "starting thread!");
				MessageBuffer nextEvent = null;
				while (isRunning == true) {
					
						while ( INSTANCE.transferQ.hasNextEvent() == true) {
							nextEvent = INSTANCE.transferQ.getNextEvent();
							Event event = Coder.decodeEvent(nextEvent);
							dbHelper.createEvent(event.getMsgId(), event.getEventType().getValueOf(), event.getTimestamp().getTime(),
									event.getTargetLoc().getLatitude(), event.getTargetLoc().getLongitude(),
									event.getOrigLoc().getLatitude(), event.getOrigLoc().getLongitude());
							double relVal = 0;
							if (event.getEventType() == EventType.EVACUATE_ALL) {
								relVal = 100.0;  // always important no matter what!!
							}
							else relVal = INSTANCE.aor.getRelevance(event.getTargetLoc());
							uiDisplay.displayMessage(event.getMsgId() + "(received) (" + relVal + "%)", relVal);
							Log.d(TAG, "Received an event " + event.getMsgId() + " Relevance: " + relVal + "%");
							InterestEngine.this.notifyReceivers(event);
						}
//						Log.d(TAG, "*YAWN* Going to sleep...  :)");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							Log.d(TAG, "Sleep was interrupted!  What the heck!");
							e.printStackTrace();
						}
					
				}
				Log.d(TAG, "isRunning has changed to false -- ending interest engine");
			}			
		}).start();
	}

	
}
