package edu.gmu.contextdb.utils;

public interface Constants {
    
    public static final String INITIALIZE_NETWORK = "edu.gmu.hodum.INITIALIZE_NETWORK";
    public static final String START_RECEIVING = "edu.gmu.hodum.START_RECEIVING";
    public static final String SEND_DATA = "edu.gmu.hodum.SEND_DATA";
    public static final String SHUTDOWN_NETWORK = "edu.gmu.hodum.SHUTDOWN_NETWORK";
    public static final String NETWORK_INITIALIZED = "edu.gmu.hodum.NETWORK_INITIALIZED";
    public static final String RECEIVE_DATA = "edu.gmu.hodum.RECEIVE_DATA";
    public static final String DEBUG_RECEIVE_DATA = "edu.gmu.hodum.DEBUG_RECEIVE_DATA";
    
    
    public static final int RECEIVE_DATA_MSG = 1;
    public static final int DEBUG_RECEIVE_DATA_MSG = 2;
    
    public static final String PERSON_OBSERVATION = "person";
    
    //Protocol message types
    public static final int HELLO_MESSAGE = 112;
    public static final int HELLO_RESPONSE = 113;
    public static final int SEND_TO_ALL = 114;
    public static final int SEND_TO_SPECIFIC = 115;
    public static final int RECEIVED_SPECIFIC = 116;
    public static final int RECEIVED_BROADCAST = 117;
    public static final int ACK_MESSAGE = 118;  // Acknowledge message for a sent packet
    public static final int CONNECT_MESSAGE = 119;
    public static final int QUE_PASA = 120;
    public static final int QUE_PASA_RESPONSE = 121;
    
    public static final int UNAVAILABLE = 1; // - The underlying hardware enabling the comm mechanism is unavailable (cannot be turned on)
	public static final int DISABLED = 2;// - The underlying hardware enabling the comm mechanism is available but disabled (needs to be turned on)
	public static final int ENABLED = 3;// - ENABLED - The underlying hardware enabling the comm mechanism is available and enabled
	public static final int ACTIVE = 4;// -  The underlying hardware is available, enabled, and currently being actively used to
	 								   // send or receive messages, as well as establish connections (if relevant to this protocol).
    
}
