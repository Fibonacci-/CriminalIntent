package com.helwigdev.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Tyler on 1/27/2015.
 */
public class CrimePagerActivity extends ActionBarActivity implements CrimeFragment.Callbacks{
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager= new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		mCrimes = CrimeLab.get(this).getCrimes();

		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public Fragment getItem(int i) {
				return CrimeFragment.newInstance((mCrimes.get(i).getId()));
			}

			@Override
			public int getCount() {
				return mCrimes.size();
			}
		});

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {}

			@Override
			public void onPageSelected(int i) {
				Crime crime = mCrimes.get(i);
				if(crime.getTitle() != null){
					setTitle(crime.getTitle());
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {}
		});

		UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		for(int i = 0; i < mCrimes.size(); i++){
			if(mCrimes.get(i).getId().equals(crimeId)){
				mViewPager.setCurrentItem(i);
				break;
			}
		}

	}


	@Override
	public void onCrimeUpdated(Crime crime) {

	}
}
