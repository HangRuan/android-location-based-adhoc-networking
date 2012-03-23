package edu.gmu.hodum.service_client.util;




import edu.gmu.hodum.service_client.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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