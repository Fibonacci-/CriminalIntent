package com.helwigdev.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

/**
 * Created by Tyler on 1/29/2015.
 */
public class SuperMassiveChallengePickerDateTimePickerFragment extends DialogFragment {

	public static final int EXTRA_CODE_DATE = 100;
	public static final int EXTRA_CODE_TIME = 101;
	public static final String EXTRA_CODE = "picker";


	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker, null);

		Button bDate = (Button) v.findViewById(R.id.b_superpicker_date);
		Button bTime = (Button) v.findViewById(R.id.b_superpicker_time);

		bDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendResult(Activity.RESULT_OK, EXTRA_CODE_DATE);
				dismiss();
			}
		});
		bTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendResult(Activity.RESULT_OK, EXTRA_CODE_TIME);
				dismiss();
			}
		});

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.pick_date_time)
				.create();
	}


	private void sendResult(int resultCode, int codeReturn) {
		if (getTargetFragment() == null)
			return;
		Intent i = new Intent();
		i.putExtra(EXTRA_CODE, codeReturn);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}
