package com.helwigdev.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Tyler on 2/6/2015.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CrimeCameraFragment();
	}
}
