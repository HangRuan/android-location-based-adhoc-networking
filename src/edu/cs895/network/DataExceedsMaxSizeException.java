package edu.cs895.network;

public class DataExceedsMaxSizeException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DataExceedsMaxSizeException(){
            
    }
    
    public DataExceedsMaxSizeException(String message){
            super(message);
    }

}

