package com.helwigdev.criminalintent;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tyler on 2/6/2015.
 */

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";

	private Camera mCamera;
	private SurfaceView mSurfaceView;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

		Button bTakePicture = (Button) v.findViewById(R.id.b_crime_camera_take_picture);
		bTakePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		mSurfaceView = (SurfaceView) v.findViewById(R.id.sv_crime_camera);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//reqd for pre-3.0 devices, is deprecated

		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				//tell camera to use this surface for previews
				try{
					if(mCamera != null){
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException e){
					Log.e(TAG, "Error setting up preview display" );
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if(mCamera == null) return;

				Camera.Parameters params = mCamera.getParameters();
				Camera.Size s = getBestSize(params.getSupportedPreviewSizes(), width, height);
				params.setPreviewSize(s.width, s.height);
				mCamera.setParameters(params);
				try{
					mCamera.startPreview();
				} catch (Exception e){
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(mCamera != null){
					mCamera.stopPreview();
				}
			}
		});

		return v;
	}

	private Camera.Size getBestSize(List<Camera.Size> sizes, int width, int height){
		Camera.Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for(Camera.Size s : sizes){
			int area = s.width * s.height;
			if(area > largestArea){
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if(mCamera != null){
			mCamera.release();
			mCamera = null;
		}
	}
}
