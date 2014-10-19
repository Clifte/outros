package br.com.sempreSol.cam360;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class OrientationSensorControler implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private ArrayList<int[]> data = new ArrayList<int[]>();
	private long initialTime = 0;

	
	
	
	public OrientationSensorControler(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	
	public void storeData(String filePath) throws IOException {
		mSensorManager.unregisterListener(this);
		
		DataOutputStream fos = new DataOutputStream(new FileOutputStream(filePath + ".osd"));
		

		fos.writeInt(this.data.size());
		for (Iterator<int[]> iterator = data.iterator(); iterator.hasNext();) {
			int[] currValue = (int[]) iterator.next();
			
			fos.writeInt(currValue[0]);
			fos.writeInt(currValue[1]);
			fos.writeInt(currValue[2]);
			fos.writeInt(currValue[3]);

		}
		fos.flush();
		fos.close();
	}

	public void startRecording() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		initialTime = System.currentTimeMillis();
	}
	
	  @Override
	  public void onSensorChanged(SensorEvent event) {
	    int sensorValuesCopy[] = new int[4];
	    
	    sensorValuesCopy[0] =  (int) Math.floor(event.values[0]/10); //azimuth
	    sensorValuesCopy[1] =  (int) Math.floor(event.values[1]/10); //pitch
	    sensorValuesCopy[2] =  (int) Math.floor(event.values[2]/10); //angle
	    sensorValuesCopy[3] =  (int) (System.currentTimeMillis() - initialTime) / 10; //Time from begin
	    Log.d("OrientationSensorControler", " " + sensorValuesCopy[0] + ' ' + sensorValuesCopy[1] + ' ' + sensorValuesCopy[2] + ' ' +sensorValuesCopy[3]);
	    this.data.add( sensorValuesCopy );
	  }


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	  
	  

}
