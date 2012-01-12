package edu.cs895.context;

import edu.cs895.message.Coder;

import edu.cs895.message.Event;
import edu.cs895.message.EventType;
import edu.cs895.message.MessageBuffer;

public class CurrentContext {
	
	private long state;
	private static long IDLE = 1;
	private static long FIGHTING_FIRE = 2;
	private static long EVACUATING = 3;
	
	public CurrentContext()
	{
		state = IDLE;
	}
	
	synchronized public boolean shouldNotify(Event evt)
	{
		boolean ret = true;
		
		
		if(evt.getEventType() == EventType.EVACUATE)
		{
			state = EVACUATING;
			ret = true;
		}
		else if(state == IDLE)
		{
			if(evt.getEventType() == EventType.FIRE)
			{
				state = FIGHTING_FIRE;
				ret =  true;
			}
		}
		else if(state == FIGHTING_FIRE)
		{
			ret = false;
		}
		return ret;
	}
	
	public void resetState()
	{
		state = IDLE;
	}

}
