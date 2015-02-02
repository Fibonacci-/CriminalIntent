package com.helwigdev.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Tyler on 1/15/2015.
 */
public class CrimeLab {
	private static CrimeLab sCrimeLab;
	private ArrayList<Crime> mCrimes;
	private Context mAppContext;

	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mCrimes = new ArrayList<>();

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

	public void addCrime(Crime c){
		mCrimes.add(c);
	}
}
