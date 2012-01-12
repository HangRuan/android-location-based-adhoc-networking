package edu.cs895.util;




import edu.cs895.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;

public class MyProgressDialog extends Dialog {

	public  void show(CharSequence title,
			CharSequence message) {
		show(title, message, false);
	}

	public  void show(CharSequence title,
			CharSequence message, boolean indeterminate) {
		show(title, message, indeterminate, false, null);
	}

	public  void show(CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		show(title, message, indeterminate, cancelable, null);
	}

	public  void show(CharSequence title,
			CharSequence message, boolean indeterminate,
			boolean cancelable, OnCancelListener cancelListener) {

		this.setTitle(title);
		this.setCancelable(cancelable);
		this.setOnCancelListener(cancelListener);
		this.show();

	}

	public MyProgressDialog(Context context) {
		super(context);//, R.style.transparentDialog);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.overlay_layout, null);
        setContentView(vg);
	}
}