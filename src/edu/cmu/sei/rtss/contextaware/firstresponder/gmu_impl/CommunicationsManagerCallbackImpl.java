package edu.cmu.sei.rtss.contextaware.firstresponder.gmu_impl;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.sei.rtss.contextaware.firstresponder.CommunicationsManagerCallback;

public class CommunicationsManagerCallbackImpl implements
CommunicationsManagerCallback {

	private static CommunicationsManagerCallbackImpl instance = null;
	List<CommunicationsManagerCallback> interested = new ArrayList<CommunicationsManagerCallback>();
	
	List<String> users = new ArrayList<String>();
	List<String> connected = new ArrayList<String>();

	public List<String> getUsers()
	{
		return users;
	}
	
	public List<String> getConnected()
	{
		return connected;
	}
	
	public static CommunicationsManagerCallbackImpl getInstance()
	{
		if(instance == null)
		{
			instance = new CommunicationsManagerCallbackImpl();
		}
		return instance;
	}
	
	public void registerInterest(CommunicationsManagerCallback interest)
	{
		interested.add(interest);
	}
	
	public void unregisterInterest(CommunicationsManagerCallback interest)
	{
		interested.remove(interest);
	}
	
	@Override
	public void reportNewUser(String externalUserID, String userName) {
		// TODO Auto-generated method stub
		for(CommunicationsManagerCallback interests:interested)
		{
			interests.reportNewUser(externalUserID, userName);
		}
		users.add(externalUserID);
	}

	@Override
	public void reportConnectionEnabled(String externalUserID) {
		for(CommunicationsManagerCallback interests:interested)
		{
			interests.reportConnectionEnabled(externalUserID);
		}
		connected.add(externalUserID);
	}

	@Override
	public void reportConnectionDisabled(String externalUserID) {
		for(CommunicationsManagerCallback interests:interested)
		{
			interests.reportConnectionDisabled(externalUserID);
		}
		connected.remove(externalUserID);
	}

	@Override
	public void reportConnectionAttemptFailed(String externalUserID) {
		for(CommunicationsManagerCallback interests:interested)
		{
			interests.reportConnectionAttemptFailed(externalUserID);
		}

	}

	@Override
	public void reportMessageReceived(String externalUserID,
			byte[] messagePayload, int payloadSize) {
		for(CommunicationsManagerCallback interests:interested)
		{
			interests.reportMessageReceived(externalUserID, messagePayload, payloadSize);
		}
	}

}
