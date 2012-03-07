package edu.gmu.hodum.sei.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import edu.gmu.hodum.sei.R;

import android.content.Context;
import android.location.Location;


public class Util {

	static int[] resources = {R.raw.script_hero, R.raw.adhoc_load, R.raw.adhoc_ini, R.raw.iwconfig,
		R.raw.libhardware_legacy, R.raw.script_nexus, R.raw.tiwlan, R.raw.wpa_supplicant, R.raw.script_evo};
 
	static String[] fileNames = {Constants.HERO_SCRIPT, "adhoc_load", "adhoc.ini", "iwconfig", 
		"libhardware_legacy.so" ,"script_nexus", "tiwlan.ini", "wpa_supplicant", "script_evo"};
	static public void copyScripts(Context ctxt) {

		for(int i=0; i<resources.length;i++)
		{


			//Open your local db as the input stream
			InputStream myInput = ctxt.getResources().openRawResource(resources[i]);

			// Path to the just created empty db
			String outFileName = ctxt.getFilesDir() + "/" + fileNames[i];

			//Open the empty db as the output stream
			OutputStream myOutput;
			try {
				myOutput = new FileOutputStream(outFileName);
				//transfer bytes from the inputfile to the outputfile
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer))>0){
					myOutput.write(buffer, 0, length);
				}
				//Close the streams
				myOutput.flush();
				myOutput.close();
				myInput.close();

				String myStringArray[]= {"chmod","777",outFileName};

				Process process = Runtime.getRuntime().exec(myStringArray);
 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} 
}
