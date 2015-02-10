package com.helwigdev.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tyler on 2/6/2015.
 */

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = "com.helwigdev.criminalintent" +
			".photo_filename";

	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mProgressContainer;
	OrientationEventListener mOrientationEventListener;

	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			mProgressContainer.setVisibility(View.VISIBLE);

		}
	};

	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String filename = UUID.randomUUID().toString() + ".jpg";
			//save jpeg data to disk
			FileOutputStream fos = null;
			boolean success = true;
			try {
				fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error saving file data " + filename, e);
				success = false;
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (Exception e) {
					Log.e(TAG, "Error closing file: " + filename, e);
					success = false;
				}
			}
			if (success) {
				Intent i = new Intent();
				i.putExtra(EXTRA_PHOTO_FILENAME, filename);

				getActivity().setResult(Activity.RESULT_OK, i);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			mOrientationEventListener.disable();

			getActivity().finish();
		}
	};


	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

		mProgressContainer = v.findViewById(R.id.fl_crime_camera_progress);
		mProgressContainer.setVisibility(View.INVISIBLE);

		Button bTakePicture = (Button) v.findViewById(R.id.b_crime_camera_take_picture);
		bTakePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});
		mSurfaceView = (SurfaceView) v.findViewById(R.id.sv_crime_camera);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//reqd for pre-3.0 devices,
		// is deprecated

		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				//tell camera to use this surface for previews
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview display");
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if (mCamera == null) {
					return;
				}

				Camera.Parameters params = mCamera.getParameters();
				Camera.Size s = getBestSize(params.getSupportedPreviewSizes(), width, height);
				params.setPreviewSize(s.width, s.height);
				s = getBestSize(params.getSupportedPictureSizes(), width, height);
				params.setPictureSize(s.width, s.height);
				mCamera.setParameters(params);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}
		});


		mOrientationEventListener = new OrientationEventListener
				(getActivity()) {

			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@Override
			public void onOrientationChanged(int orientation) {
				Log.d(TAG, "Orientation changed to " + orientation);

				if (orientation == ORIENTATION_UNKNOWN || mCamera == null) {
					return;
				}

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
					return;
				}

				Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(0, info);
				orientation = (orientation + 45) / 90 * 90;
				int rotation = 0;
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				} else { // back-facing camera
					rotation = (info.orientation + orientation) % 360;
				}
				Camera.Parameters params = mCamera.getParameters();
				params.setRotation(rotation);
				Log.d(TAG, "Setting camera rotation to " + rotation);
				mCamera.setParameters(params);
			}
		};
		mOrientationEventListener.enable();

		return v;
	}

	private Camera.Size getBestSize(List<Camera.Size> sizes, int width, int height) {
		Camera.Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Camera.Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
}
