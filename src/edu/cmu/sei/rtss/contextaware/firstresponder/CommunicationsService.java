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

public interface CommunicationsService {

	/**
	 * The initialize method will be invoked after Communication Service object creation and will provide the
	 * object with the callback object.  This is done so that the service can report any predefined connections
	 * to the application via the reportNewUser() method on the callback object.
	 * @param callbackObject
	 * 		This parameter is provided to the communication mechanism to provide it with a way to perform callbacks
	 * 		on the communications manager.  It is implemented as an interface to hide implementation details from the 
	 * 		mechanism.  
	 */
	public void initialize(CommunicationsManagerCallback callbackObject);
	
	
	/**
	 * The getState method will be invoked to determine if connections can be made and messages sent
	 * using this communications mechanism.
	 * @return protocolState
	 * 		The getState method will return one of four values:
	 * 		1 - UNAVAILABLE - The underlying hardware enabling the comm mechanism is unavailable (cannot be turned on)
	 * 		2 - DISABLED - The underlying hardware enabling the comm mechanism is available but disabled (needs to be turned on)
	 * 		3 - ENABLED - The underlying hardware enabling the comm mechanism is available and enabled
	 * 		4 - ACTIVE - The underlying hardware is available, enabled, and currently being actively used to
	 * 			send or receive messages, as well as establish connections (if relevant to this protocol).
	 */
	public int getState();
	
	
	/**
	 * The start() method will be invoked by the application to direct this communication mechanism to begin network
	 * services.  Specifically, at this time, the communication mechanism should begin listening for incoming messages
	 * (if non-connection-based) or incoming connection attempts (if connection-based).  It will only be called if a
	 * preceding invocation of the getState() method indicates a value of Enabled (3).  After successfully executing
	 * this method, the communication mechanism should report a value of Active (4) upon subsequent invocations of
	 * the getState() method.
	 */
	public void start();
	
	
	/**
	 * The stop() method will be invoked by the application to direct this communication mechanism to cease all
	 * communications.  This means that all established connections (for connection-based protocols) will be dropped
	 * and all activities that receive messages should be halted.  Any messages that continue to be received due to
	 * the nature of the protocol should be dropped on the ground.  After successfully executing this method,
	 * the communication mechanism should report a value of Enabled (3) upon subsequent invocations of the getState()
	 * method.
	 */
	public void stop();
	
	
	/**
	 * The connect method will be invoked by the application to establish a connection with the 
	 * target external device.  This is typically a primary function of connection-based protocols,
	 * for example BlueTooth or TCP/IP.  For non-connection-based protocols, invoking this method should result in
	 * an attempt to establish connections with available devices, and may result in 1 or more
	 * successful connections.
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 */
	public void connect(String externalUserID);
	
	
	/**
	 * The disconnect method will be invoked by the application to close an existing connection with
	 * the target external device.  This is typically a primary function of connection-based protocols,
	 * for example BlueTooth or TCP/IP.  For non-connection-based protocols, invoking this method should result in
	 * termination of communication with the target device (if possible) or termination of communication
	 * with all devices (if necessary).
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 */
	public void disconnect(String externalUserID);
	
	
	/**
	 * The sendDataToAll method will be invoked by the application when it decides to send data to all connected
	 * users.  The communication mechanism should send the byte array to all connected external devices (in
	 * connection-based protocols) and to anyone who will listen (in non-connection-based protocols).
	 * @param out
	 * 		This parameter represents the byte array of data that must be sent.  This is the total number of bytes
	 * 		to send; there is no streaming being performed.
	 */
	public void sendDataToAll(byte[] out);
	
	
	/**
	 * The sendDataToAll method will be invoked by the application when it decides to send data to a particular
	 * external user.  The communication mechanism should send the byte array to the specific external user (if possible).
	 * @param externalUserID
	 * 		This parameter represents the unique ID assigned to the external user by the communication mechanism
	 * 		in the reportNewUser() method.
	 * @param out
	 * 		This parameter represents the byte array of data that must be sent.  This is the total number of bytes
	 * 		to send; there is no streaming being performed.
	 */
	public void sendData(String externalUserID, byte[] out);
	
}
