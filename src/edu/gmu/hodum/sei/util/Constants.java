package edu.gmu.hodum.sei.util;

public interface Constants {

	// user package max size equivalent 54kb
    public static final int MAX_PACKAGE_SIZE = 54000;
    
    public static final short POINT_ADDRESS = 0;
    public static final short SQUARE_REGION_ADDRESS = 1;
    public static final short ROUND_REGION_ADDRESS = 2;
    
    
    public static final String HERO_SCRIPT="hero_script";
    public static final String NEXUS_SCRIPT1="script_nexus ";
    public static final String NEXUS_SCRIPT2="script_nexus ";
    public static final String EVO_SCRIPT="script_evo ";
    
    
    public static final String networkPrefix = "192.168.42.";
    public static final String broadcastAddress = networkPrefix + "255";
}
