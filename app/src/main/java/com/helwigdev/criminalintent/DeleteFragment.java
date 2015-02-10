package com.helwigdev.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by Tyler on 2/9/2015.
 * All code herein copyright Helwig Development 2/9/2015
 */
public class DeleteFragment extends DialogFragment {

	public static final String EXTRA_DELETE_PATH = "com.helwigdev.criminalintent.delete_path";

	public static DeleteFragment newInstance(String imagePath){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DELETE_PATH, imagePath);
		DeleteFragment fragment = new DeleteFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String filepath = getArguments().getString(EXTRA_DELETE_PATH);
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.question_delete)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int resultCode = Activity.RESULT_OK;
						File f = getActivity().getFileStreamPath(filepath);
						if (f.delete()) {
							Log.d("DELETE_FRAGMENT", "Photo deleted OK");
						} else {
							Log.e("DELETE_FRAGMENT", "Could not delete associated photo");
							resultCode = Activity.RESULT_CANCELED;
						}
						if (getTargetFragment() != null) {
							getTargetFragment().onActivityResult(getTargetRequestCode(),
									resultCode, new Intent());
						}
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (getTargetFragment() != null) {
							getTargetFragment().onActivityResult(getTargetRequestCode(),
									Activity.RESULT_CANCELED, new Intent());
						}
					}
				}).create();
	}
}
