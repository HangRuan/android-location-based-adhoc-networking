package edu.gmu.hodum.sei.gesture.util;

public class RowData
{
	protected int mId;
	protected String mTitle;
	protected String mDetail;

	public RowData(int id, String title, String detail)
	{
		mId = id;
		mTitle = title;
		mDetail = detail;
	}

	public String toString()
	{
		return mId + " " + mTitle + " " + mDetail;
	}
}
