package com.helwigdev.criminalintent;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Tyler on 2/4/2015.
 * All code herein copyright Helwig Development 2/4/2015
 */
public class CriminalIntentJSONSerializer {

	private Context mContext;
	private String mFileName;
	private static final String TAG = "Serializer";

	public CriminalIntentJSONSerializer(Context c, String f){
		mContext = c;
		mFileName = f;
	}

	public ArrayList<Crime> loadCrimes() throws IOException, JSONException{
		ArrayList<Crime> crimes = new ArrayList<>();
		BufferedReader reader = null;
		try{
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){//we can read and write the external storage
				File f = new File(Environment.getExternalStorageDirectory(), mFileName);//CHALLENGE 17
				//not anymore InputStream in = mContext.openFileInput(mFileName);
				InputStream in = new FileInputStream(f);
				reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder jsonString = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null){
					jsonString.append(line);
				}
				JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
				for(int i = 0; i < array.length(); i++){
					crimes.add(new Crime(array.getJSONObject(i)));
				}
			} else {
				Log.e(TAG, "Could not open external storage");
			}

		} catch (FileNotFoundException e){
			//ignore - just means there haven't been any crimes saved yet
		} finally {
			if(reader != null){
				reader.close();
			}
		}
		return crimes;
	}

	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException{
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//we can read and write the external storage
			JSONArray array = new JSONArray();
			for (Crime c : crimes) {
				array.put(c.toJSON());
			}

			Writer writer = null;
			try {
				File f = new File(Environment.getExternalStorageDirectory(), mFileName);
				//nope nope nope OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
				OutputStream out = new FileOutputStream(f);//CHALLENGE 17
				writer = new OutputStreamWriter(out);
				writer.write(array.toString());
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} else {
			Log.e(TAG, "Could not open external storage");
		}
	}

}
