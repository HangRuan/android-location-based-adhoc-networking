package edu.gmu;

import edu.gmu.content.ContextContentProvider;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class CreateDatabaseActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ContextDataProvider db = new ContextDataProvider(this);
        
    }
}