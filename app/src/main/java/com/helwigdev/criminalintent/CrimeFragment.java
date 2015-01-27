package com.helwigdev.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by Tyler on 1/14/2015.
 */
public class CrimeFragment extends Fragment {

	public static final String EXTRA_CRIME_ID = "com.helwigdev.criminalintent.crime_id";
	private Crime mCrime;
    private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

	public static CrimeFragment newInstance(UUID crimeId){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mDateButton = (Button) v.findViewById(R.id.b_crime_date);
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.ck_crime_solved);

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

		mDateButton.setText(mCrime.getDate().toString());
		mDateButton.setEnabled(mCrime.isSolved());

		mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});

        return v;
    }
}
