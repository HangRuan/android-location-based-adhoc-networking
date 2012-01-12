package edu.cs895.message;

public enum EventType {
	FIRE("Fire", 1),
	WATER_DUMP("Water Dump", 2),
	SMOKE("Smnoke", 3),
	MEDICAL("Medical", 4), 
	BLOCKAGE("Blockage", 5), 
	EVACUATE("Evacuate", 6), 
	FLOODING("Flooding", 7),
	EVACUATE_ALL("All Evacuate", 8),
	UNKNOWN("Unknown", 9);
	
	private String text;
	private int value;
	
	private EventType(String text, int value) { this.text = text; this.value = value; }
	public String getText() { return this.text; }
	public static EventType getFromValue(int inVal){
		if (inVal == FIRE.getValueOf())
			return FIRE;
		else if (inVal == WATER_DUMP.getValueOf())
			return WATER_DUMP;
		else if (inVal == SMOKE.getValueOf())
			return SMOKE;
		else if (inVal == MEDICAL.getValueOf())
			return MEDICAL;
		else if (inVal == BLOCKAGE.getValueOf())
			return BLOCKAGE;
		else if (inVal == EVACUATE.getValueOf())
			return EVACUATE;
		else if (inVal == FLOODING.getValueOf())
			return FLOODING;
		else if (inVal == EVACUATE_ALL.getValueOf())
			return EVACUATE_ALL;
		return UNKNOWN;
	}
	
	public int getValueOf(){
		return this.value;
	}
}
