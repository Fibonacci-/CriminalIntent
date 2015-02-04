package com.helwigdev.criminalintent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.app.ActionBar;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by Tyler on 1/15/2015.
 */
public class CrimeListFragment extends ListFragment {
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.get(getActivity()).getCrimes();

		setHasOptionsMenu(true);
		setRetainInstance(true);
		mSubtitleVisible = false;
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if(mSubtitleVisible){
				((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
			}
		}
		return v;
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(i);
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {
		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//if view not passed, inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_crime, null);
			}
			Crime c = getItem(position);
			TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_crime_list_item_title);
			TextView tvDate = (TextView) convertView.findViewById(R.id.tv_crime_list_item_date);
			CheckBox ckSolved = (CheckBox) convertView.findViewById(R.id.ck_crime_list_item_solved);

			DateFormat df = DateFormat.getDateInstance();

			tvTitle.setText(c.getTitle());
			tvDate.setText(df.format(c.getDate()));
			ckSolved.setChecked(c.isSolved());

			return convertView;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitle  = menu.findItem(R.id.menu_item_show_subtitle);
		if(mSubtitleVisible && showSubtitle != null){
			showSubtitle.setTitle(R.string.show_subtitle);
		}
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_new_crime:
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent i = new Intent(getActivity(), CrimePagerActivity.class);
				i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
				startActivityForResult(i, 0);
				return true;
			case R.id.menu_item_show_subtitle:

				if (((ActionBarActivity) getActivity()).getSupportActionBar().getSubtitle() == null) {//this is a bunch of bullshit caused by api 8 and not recognized by the book
					((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
					item.setTitle(R.string.hide_subtitle);
					mSubtitleVisible = true;
				} else {
					((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(null);
					item.setTitle(R.string.show_subtitle);
					mSubtitleVisible = false;
				}

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}








