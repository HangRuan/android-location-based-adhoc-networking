/**
* Copyright 2011 Carnegie Mellon University
*
* This material is being created with funding and support by the Department of Defense under Contract No. FA8721-05-C-0003 
 * with Carnegie Mellon University for the operation of the Software Engineering Institute, a federally funded research and 
 * development center.  As such, it is considered an externally sponsored project  under Carnegie Mellon University's 
 * Intellectual Property Policy.
*
* This material may not be released outside of Carnegie Mellon University without first contacting permission@sei.cmu.edu.
*
* This material makes use of the following Third-Party Software and Libraries which are used pursuant to the referenced 
 * Licenses.  Any modification of Third-Party Software or Libraries must be in compliance with the applicable license 
 * (and only if permitted):
* 
 *    Android
*    Source: http://source.android.com/source/index.html
*    License: http://source.android.com/source/licenses.html
* 
* Unless otherwise stated in any Third-Party License or as otherwise required by applicable law or agreed to in writing, 
 * All Third-Party Software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express 
 * or implied.
*/

package edu.cmu.sei.rtss.contextaware.firstresponder;

import android.content.Context;

public interface CommunicationsManagerCallback {

	/**
	 * The reportNewUser method should be invoked in two cases: 
	 * 1. Upon startup, the communication mechanism should invoke this method to inform the application of any predefined
	 * external users that it is aware of.
	 * 2. Upon detecting a previously unknown external user, the communication mechanism should invoke this method to
	 * inform the application of the new external user.
	 * *** IMPORTANT NOTE: It is the responsibility of the communication mechanism to ONLY report new users from any
	 * predefined list that are NOT the local user.  Therefore the communication mechanism must get (for example) it's
	 * local BlueTooth address and compare to the addresses in a given predefined list.  For some communication mechanisms,
	 * this situation may not exist (such as GeoWIFI) but if the situation exists, the application will not work properly
	 * if the local user is offered to the application as a new user. 
	 * *** Implementation Note: This method will likely be invoked in concert with other methods.  For example, for
	 * non-connection-based protocols, a single received message may result in the communication service needing to
	 * invoke reportNewUser, followed by reportConnectionEnabled, followed by reportMessageReceived.  In this situation,
	 * the ordering matters; reportNewUser must be called first to ensure that the entry is created for the new user.
	 * @param externalUserID
	 * 		This parameter represents a unique ID, used by this particular communication mechanism, to identify the new
	 * 		external user.  It can be any unique ID the mechanism wishes to use; creating a new UUID for this purpose is
	 * 		the suggested method of implementation.  This unique ID will then be used by all other callbacks to the
	 * 		application and calls by the application to the communication mechanism to uniquely identify a specific
	 * 		external user.
	 * @param userName
	 * 		This parameter represents the known name for a particular user.  In the case of predefined connections, this
	 * 		value should be available and thus provided to the application.  In the case of unknown external users that
	 * 		are identified at runtime, this value will most likely be unknown.  In this case, the communication mechanism
	 * 		should just return null. 
	 */
	public void reportNewUser(String externalUserID, String userName);

	
	/**
	 * The reportConnectionEnabled method should be invoked in the event where a new connection is established
	 * to an external device.  In connection-based protocols, this will be the result of a successful connection
	 * attempt.  In non-connection-based protocols, this will be the result of successful communication with an
	 * external device which either had not been communicated with since application execution time, or which
	 * has resumed communication after some (communication-mechanism defined) period of no communication.
	 * This will typically be the result of processing performed in response to execution of the connect() 
	 * method of the object implementing the CommunicationsService interface. 
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 */
	public void reportConnectionEnabled(String externalUserID);
	
	
	/**
	 * The reportConnectionDisabled method should be invoked in the event where an existing connection is 
	 * terminated between the local device and an external device.  In connection-based protocols, this will be
	 * the result of a successful attempt to close an open connection.  In non-connection-based protocols, this 
	 * will be the result of the cessation of communication with an external device which had be previously been
	 * communicating with the local device at some minimum (communication-mechanism defined) rate.
	 * This will typically be the result of processing performed in response to execution of the disconnect() 
	 * method of the object implementing the CommunicationsService interface. 
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 */
	public void reportConnectionDisabled(String externalUserID);
	
	
	/**
	 * The reportConnectionAttemptFailed method should be invoked in the event where an attempt to establish
	 * communication with a specific device has failed.  In connection-based protocols, this will be
	 * the result of a failed attempt to open a connection with an external device.  In non-connection-based 
	 * protocols, this may or may not be called at all, depending on if the protocol can determine whether or
	 * not the connection attempt has actually failed.  For protocols that cannot determine whether a connection
	 * attempt has failed, this method is not required to be called.
	 * For protocols where this is appropriate, this method will typically be the result of processing performed 
	 * in response to execution of the connect() method of the object implementing the CommunicationsService interface. 
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 */
	public void reportConnectionAttemptFailed(String externalUserID);
	
	
	/**
	 * The reportMessageReceived method should be invoked in the event where a message has been received
	 * from an external device.  It should be called to report the event, and the message content,
	 * to the application.
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 * @param messagePayload
	 * 		This is the content of the message.  It is expected to be a simple byte array.  The translation
	 * 		of the byte array into meaningful data will be handled by the application and should be transparent
	 * 		to the implementation of the communication mechanism.
	 * @param payloadSize
	 * 		This is the size, in bytes, of the data within payload byte array.  It is typically the return value
	 * 		of the InputStream.read() method; i.e. not the size of the array, but the number of bytes read.
	 */
	public void reportMessageReceived(String externalUserID, byte[] messagePayload, int payloadSize);
	
}
