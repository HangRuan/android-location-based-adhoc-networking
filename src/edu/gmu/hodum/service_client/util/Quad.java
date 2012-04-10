package edu.gmu.hodum.service_client.util;

public class Quad <S,T,U,V> {
    private final S mFirst;
    private final T mSecond;
    private final U mThird;
    private final V mFourth;

    // quad
    public Quad(S first, T second, U third, V fourth) {
        this.mFirst = first;
        this.mSecond = second;
        this.mThird = third;
        this.mFourth = fourth;
    }

    /**
     * Return the first item in the quad
     *
     * @return the first item in the quad
     */
    public S getFirst() {
        return mFirst;
    }

    /**
     * Return the second item in the quad
     *
     * @return the second item in the quad
     */
    public T getSecond() {
        return mSecond;
    }
    
    /**
     * Return the third item in the quad
     *
     * @return the third item in the quad
     */
    public U getThird() {
        return mThird;
    }
    
    /**
     * Return the fourth item in the quad
     *
     * @return the fourth item in the quad
     */
    public V getFourth() {
        return mFourth;
    }
    
    public boolean equals(Quad<S,T,U,V> right)
    {
    	boolean ret = false;
    	if(mFirst.equals(right.mFirst) && mSecond.equals(right.mSecond) && mThird.equals(right.mThird) && mFourth.equals(right.mFourth))
    	{
    		ret = true;
    	}
    	return ret;
    }
}
