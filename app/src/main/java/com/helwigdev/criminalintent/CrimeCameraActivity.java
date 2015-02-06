package com.helwigdev.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Tyler on 2/6/2015.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//hide window title
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		//hide status bar etc
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
	}

	@Override
	protected Fragment createFragment() {
		return new CrimeCameraFragment();
	}
}
