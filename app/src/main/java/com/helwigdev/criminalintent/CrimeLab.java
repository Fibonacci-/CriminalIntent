package com.helwigdev.criminalintent;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


public class CrimeLab {
	private static CrimeLab sCrimeLab;
	private ArrayList<Crime> mCrimes;
	private Context mAppContext;

	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	private CriminalIntentJSONSerializer mSerializer;


	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

		try {
			mCrimes = mSerializer.loadCrimes();
		} catch (Exception e) {
			mCrimes = new ArrayList<>();
			Log.e(TAG, "Error loading crimes: " + e);
		}
	}

	public static CrimeLab get(Context c) {
		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}

	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}

	public Crime getCrime(UUID id) {
		for (Crime c : mCrimes) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;

	}

	public boolean saveCrimes() {
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG, "Crimes saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error saving crimes: " + e);
			return false;
		}
	}

	public void addCrime(Crime c) {
		mCrimes.add(c);
	}

	public void deleteCrime(Crime c){
		if(c.getPhoto() != null){
			String file = c.getPhoto().getFilename();
			File f = mAppContext.getApplicationContext().getFileStreamPath(file);
			if(f.delete()) {
				Log.d(TAG, "Deleted crime photo " + file);
			} else {
				Log.e(TAG, "Failed to delete photo");
			}
		}
		mCrimes.remove(c);
	}
}
