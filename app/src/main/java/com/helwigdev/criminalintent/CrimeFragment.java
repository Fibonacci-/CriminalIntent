package com.helwigdev.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Tyler on 1/14/2015.
 */
public class CrimeFragment extends Fragment {

	public static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "com.helwigdev.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_TIME = "time";
	private static final String DIALOG_PICKER = "picker";
	private static final String DIALOG_IMAGE = "image";
	private static final String DIALOG_DELETE = "delete_photo";
	private static final int REQUEST_DATE = 5;
	private static final int REQUEST_TIME = 6;
	private static final int REQUEST_PICKER = 7;
	private static final int REQUEST_PHOTO = 8;
	private static final int REQUEST_DELETE = 9;

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.crime_list_item_context, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.mi_delete_crime:
				CrimeLab.get(getActivity()).deleteCrime(mCrime);
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mDateButton = (Button) v.findViewById(R.id.b_crime_date);
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.ck_crime_solved);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null && getActivity().getActionBar() != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		mTitleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//nothing to see here
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
				//move along citizen
			}
		});

		mTitleField.setText(mCrime.getTitle());

		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				SuperMassiveChallengePickerDateTimePickerFragment dialog = new SuperMassiveChallengePickerDateTimePickerFragment();
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_PICKER);
				dialog.show(fm, DIALOG_PICKER);
			}
		});

		mSolvedCheckBox.setChecked(mCrime.isSolved());

		mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});

		mPhotoButton = (ImageButton) v.findViewById(R.id.ib_crime);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});

		mPhotoView = (ImageView) v.findViewById(R.id.iv_crime);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if(p == null){
					return;
				}

				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});
		mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(mCrime.getPhoto() == null){
					return false;
				} else {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					String filename = mCrime.getPhoto().getFilename();
					DeleteFragment df = DeleteFragment.newInstance(filename);
					df.setTargetFragment(CrimeFragment.this, REQUEST_DELETE);
					df.show(fm, DIALOG_DELETE);

					return true;
				}
			}
		});

		//disable if no camera
		PackageManager pm = getActivity().getPackageManager();
		boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
				(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
						Camera.getNumberOfCameras() > 0);
		if (!hasCamera) {
			mPhotoButton.setEnabled(false);
		}



		return v;
	}

	private void showPhoto(){
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if(p != null){
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}

	@Override
	public void onStart(){
		super.onStart();
		showPhoto();
	}

	private void updateDate() {
		DateFormat df = DateFormat.getDateInstance();
		mDateButton.setText(mCrime.getDate().toString());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(getActivity(), "got callback: requestCode = " + requestCode + " resultCode = " + resultCode, Toast.LENGTH_SHORT).show();
		if (resultCode != Activity.RESULT_OK) {
			Toast.makeText(getActivity(), "request fail", Toast.LENGTH_SHORT).show();
			return;
		}

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
			Toast.makeText(getActivity(), "everything happened as it should", Toast.LENGTH_SHORT).show();
		} else if (requestCode == REQUEST_TIME) {
			Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			mCrime.setDate(date);
			updateDate();
		} else if (requestCode == REQUEST_PICKER) {
			//launch date or time depending on data
			int code = (int) data.getSerializableExtra(SuperMassiveChallengePickerDateTimePickerFragment.EXTRA_CODE);
			if (code == SuperMassiveChallengePickerDateTimePickerFragment.EXTRA_CODE_DATE) {
				//launch date picker
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			} else {
				//launch time picker
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
				dialog.show(fm, DIALOG_TIME);
			}
		} else if (requestCode == REQUEST_PHOTO) {
			String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null) {
				Photo p = new Photo(filename);
				if(mCrime.getPhoto() != null){
					String file = mCrime.getPhoto().getFilename();
					File f = getActivity().getFileStreamPath(file);
					if(f.delete()) {
						Log.d(TAG, "Deleted previous photo " + file);
					} else {
						Log.e(TAG, "Failed to delete photo");
					}
				}
				mCrime.setPhoto(p);
				showPhoto();
			}
		} else if (requestCode == REQUEST_DELETE){
			mPhotoView.setImageDrawable(null);
			mCrime.setPhoto(null);//CHALLENGE 20
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

	@Override
	public void onStop(){
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
}
