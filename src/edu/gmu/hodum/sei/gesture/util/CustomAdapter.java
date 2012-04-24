package edu.gmu.hodum.sei.gesture.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.gmu.hodum.sei.gesture.R;

public class CustomAdapter extends ArrayAdapter<RowData>
{
	private LayoutInflater mInflater;
	
	public CustomAdapter(Context context, LayoutInflater inflater, int resource, int textViewResourceId,
			List<RowData> objects)
	{
		super(context, resource, textViewResourceId, objects);

		this.mInflater = inflater;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		TextView title = null;
		TextView detail = null;
		RowData rowData = getItem(position);
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		title = holder.getTitle();
		title.setText(rowData.mTitle);
		detail = holder.getdetail();
		detail.setText(rowData.mDetail);
		return convertView;
	}

	private class ViewHolder
	{
		private View mRow;
		private TextView title = null;
		private TextView detail = null;

		public ViewHolder(View row)
		{
			mRow = row;
		}

		public TextView getTitle()
		{
			if (null == title)
			{
				title = (TextView) mRow.findViewById(R.id.title);
			}
			return title;
		}

		public TextView getdetail()
		{
			if (null == detail)
			{
				detail = (TextView) mRow.findViewById(R.id.detail);
			}
			return detail;
		}
	}
}