package edu.cs895.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.cs895.R;

import android.content.Context;
import android.location.Location;


public class Util {


	static public double distance(Location loc1, Location loc2, char unit)
	{
		return Util.distance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude(), unit);
	}

	static public double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	static private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	static private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	static int[] resources = {R.raw.script_hero, R.raw.adhoc_load, R.raw.adhoc_ini, R.raw.iwconfig,
		R.raw.libhardware_legacy, R.raw.script_nexus, R.raw.tiwlan, R.raw.wpa_supplicant, R.raw.script_evo};
 
	static String[] fileNames = {Constants.HERO_SCRIPT, "adhoc_load", "adhoc.ini", "iwconfig", 
		"libhardware_legacy.so" ,"script_nexus", "tiwlan", "wpa_supplicant", "script_evo"};
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

				String myStringArray[]= {"chmod","+x",outFileName};

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
