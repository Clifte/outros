package br.com.sempreSol.cam360;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class CameraControler {

	private Camera mCamera;
	private MediaRecorder mMediaRecorder;
	private String TAG;
	private CameraPreview mPreview;
	private boolean isRecording=false;
	protected static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private File mediaFile;

	
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	
	
	/** A safe way to get an instance of the Camera object. */
	public  Camera getCameraInstance(){
		
		if(mCamera==null){
		    try {
		    	mCamera = Camera.open(); // attempt to get a Camera instance
		    } catch (Exception e){
		    	Log.d("Initialize","Camera is not available (in use or does not exist)",e);
		    }
		}else{
			mCamera.stopPreview();
			mCamera.lock();
			mCamera.release();
			
			mCamera = Camera.open();
			//mCamera.unlock();
		}
		
	    return mCamera; // returns null if camera is unavailable
	}

	private boolean prepareVideoRecorder(){

	    mCamera = getCameraInstance();
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
											"CAM360_SempreSol");
		

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("CAM360_SempreSol", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else { 
			if (type == MEDIA_TYPE_VIDEO) { 
				mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
			} else {
				return null;
			}
	    }

		return mediaFile;
	}



	public CameraPreview getCameraPreview() {
		return mPreview;
	}



	public void setCameraPreview(CameraPreview mPreview) {
		this.mPreview = mPreview;
	}
	
	
	
	public void takePicture() {

		PictureCallback mPicture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

				if (pictureFile == null) {
					Log.d(TAG,
							"Error creating media file, check storage permissions: ");
					return;
				}

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (FileNotFoundException e) {
					Log.d(TAG, "File not found: " + e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, "Error accessing file: " + e.getMessage());
				}
				
				mCamera.startPreview();
			}

		};		
		
		mCamera.takePicture(null, null, mPicture);
	}



	public void record() {

		try{
			
			Log.d("GRAVANDO", "sdf");
	        if (isRecording) { 
	            // stop recording and release camera
	            mMediaRecorder.stop();  // stop the recording
	            releaseMediaRecorder(); // release the MediaRecorder object
	            mCamera.lock();         // take camera access back from MediaRecorder
	
	            // inform the user that recording has stopped
	
	            isRecording = false;
	        } else {
	            // initialize video camera
	            if (prepareVideoRecorder()) {
	                // Camera is available and unlocked, MediaRecorder is prepared,
	                // now you can start recording
	                mMediaRecorder.start();
	                // inform the user that recording has started
	
	                isRecording = true;
	            } else {
	                // prepare didn't work, release the camera
	                releaseMediaRecorder();
	                // inform user
	            }
	        }
		}catch(Exception e){
			Log.d("RECORDING","EXCEPTION",e);
		}
		
	}



	public boolean isRecording() {
		return isRecording;
	}



	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}



	public String filePath() {
		// TODO Auto-generated method stub
		return mediaFile.getAbsolutePath();
	}

}
