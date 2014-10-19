package br.com.sempreSol.cam360;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class Cam360 extends Activity {


	private CameraPreview mPreview;
	private CameraControler ccontroler;
	private Camera mCamera;
	private OrientationSensorControler orientationControler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cam360);

		loadComponents();
	}

	private void loadComponents() {

		Log.d("Initialize","Loading Components");
		
		//Create an instance of Camera
		ccontroler = new CameraControler();
		mCamera = ccontroler.getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		ccontroler.setCameraPreview(mPreview);
		
		
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		
		
		orientationControler = new OrientationSensorControler(this);
			
		
		Button captureButton = (Button) findViewById(R.id.buttonCapture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera=
				ccontroler.takePicture();
			}
		});
		
		
		Button recordButton = (Button) findViewById(R.id.buttonRecord);
		recordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(ccontroler.isRecording()){
					Toast.makeText(Cam360.this, "Stop Recording File saved in : " + ccontroler.filePath(), Toast.LENGTH_LONG).show();
					try {
						orientationControler.storeData(ccontroler.filePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					orientationControler.startRecording();
					Toast.makeText(Cam360.this, "Starting Recording", Toast.LENGTH_LONG).show();
				}
				
				// get an image from the camera=
				ccontroler.record();
			}
		});		
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.general_menu, menu);
		
	    MenuItem item1 = menu.findItem(R.id.general_menu_gallery);
	    Intent intent1 = new Intent(this, Gallery.class);
	    item1.setIntent(intent1);
	    
	    
	    
		return true;
	}
	
	
	
}
