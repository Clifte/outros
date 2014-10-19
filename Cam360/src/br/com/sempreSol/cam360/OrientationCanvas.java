package br.com.sempreSol.cam360;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

@SuppressLint("NewApi")
public class OrientationCanvas extends View  implements SensorEventListener{
	

	private File file;
	Bitmap frame = null;
	MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
	
	OrientationFileMap ofm = new OrientationFileMap();
	
	
	
	private Context context;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	
	
	public OrientationCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
		// TODO Auto-generated constructor stub
	}
	


	public OrientationCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public OrientationCanvas(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}	
	
	
	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		if(frame!= null){
			canvas.drawBitmap(frame, 0, 0, new Paint());
		}
	};
	
	public void setFrame(int n){
		int frameNumber = n * 1000;
		mediaMetadataRetriever = new MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
		frame = mediaMetadataRetriever.getFrameAtTime(frameNumber); //unit in microsecond
		mediaMetadataRetriever.release();
		 
		 this.invalidate();
	}
	
	public void setFile(File f) throws FileNotFoundException, IOException{
		file = f; 
		mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
	}

	public int getMaxFrames() {
		// TODO Auto-generated method stub
		return  Integer.parseInt( mediaMetadataRetriever.extractMetadata(mediaMetadataRetriever.METADATA_KEY_DURATION));
		
	}
	
	
	public void startOrientationView() throws IOException {
		mSensorManager.unregisterListener(this);
		loadOrientationFile();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void loadOrientationFile() throws IOException {
		File orientationFile = new File(file.getAbsolutePath() + ".osd");
		
		DataInputStream dsi = new DataInputStream(new FileInputStream(orientationFile));
		
		
		int size = dsi.readInt();
		
		
		int data[] = null;
		for (int i = 0; i < size; i++) {
			
			data = new int[4];
			data[0] = dsi.readInt();
			data[1] = dsi.readInt();
			data[2] = dsi.readInt();
			data[3] = dsi.readInt();
			ofm.put(data);
		}
		
		dsi.close();
		
	}



	private void stopOrientationView() {
		mSensorManager.unregisterListener(this);
	}
	
	


	@Override
	public void onSensorChanged(SensorEvent event) {
		int toFind[] = new int[4];
		
		toFind[0] =(int) Math.floor( event.values[0]/10);
		toFind[1] =(int) Math.floor( event.values[1]/10);
		toFind[2] =(int) Math.floor( event.values[2]/10);
		
		Log.d("OrientationSensorControler", " " + toFind[0] + ' ' + toFind[1] + ' ' + toFind[2] );	
		int value = ofm.get(toFind); 
		
		if(value != 0){
			setFrame(value);
		}
		
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class OrientationFileMap{
		Map<String, String> values = new HashMap<String, String>();
		
		public OrientationFileMap() {
			
		}
		
		private void put(int ar[]) {
			String str = Integer.toString( ar[0] ) + ',' +
					 	 Integer.toString( ar[1] ) + ',' +
					 	 Integer.toString( ar[2]=0 )
					 	 ;
			
			
			values.put(str,Integer.toString( ar[3]));
		}
		
		
		private int get(int ar[]){
			String str = Integer.toString( ar[0] ) + ',' +
				 	     Integer.toString( ar[1] ) + ',' +
				 	     Integer.toString( ar[2]=0 );
			
			String value = values.get(str)  ;
			
			if(value!=null){
				return Integer.parseInt(value);	
				
			}else{
				return 0; 
			}
			
			
		}
	}

}
