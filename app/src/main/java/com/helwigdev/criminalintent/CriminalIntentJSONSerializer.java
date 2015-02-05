package com.helwigdev.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
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

	public CriminalIntentJSONSerializer(Context c, String f){
		mContext = c;
		mFileName = f;
	}

	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException{
		JSONArray array = new JSONArray();
		for(Crime c: crimes){
			array.put(c.toJSON());
		}

		Writer writer = null;
		try{
			OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if(writer != null){
				writer.close();
			}
		}
	}

}
