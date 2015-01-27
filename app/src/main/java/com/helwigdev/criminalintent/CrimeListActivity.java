package com.helwigdev.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Tyler on 1/15/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}
}
