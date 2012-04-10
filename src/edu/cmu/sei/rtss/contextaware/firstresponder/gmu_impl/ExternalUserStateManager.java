package edu.cmu.sei.rtss.contextaware.firstresponder.gmu_impl;

import java.util.HashMap;
import java.util.Map;

public class ExternalUserStateManager {

	public static int NEVER_DISCOVERED = 0;
	public static int DISCONNECTED = 1;
	public static int CONNECTED = 2;
	
	private Map<String,Integer> userState = new HashMap<String,Integer>();
	
	public int newUser(String userName)
	{
		
		Integer oldState = userState.get(userName);
		if(oldState != null && (oldState == CONNECTED || oldState == DISCONNECTED))
		{
			oldState = userState.put(userName, CONNECTED);
		}
		else
		{
			userState.put(userName, DISCONNECTED);
			oldState = new Integer(NEVER_DISCOVERED);
		}
		return oldState;
	}
	
	public int disconnectUser(String userName)
	{
		Integer ret = userState.put(userName, DISCONNECTED);
		if(ret == null)
		{
			ret = new Integer(NEVER_DISCOVERED);
		}
		return ret;
	}
}