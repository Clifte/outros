package br.com.sempreSol.cam360;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class PictureViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_view);
		
		
		try{
			Intent myIntent = getIntent();
			final String path =  myIntent.getStringExtra("file");
			
			final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
			final OrientationCanvas picCanvas = (OrientationCanvas) findViewById(R.id.pictureCanvas);
			picCanvas.setFile(new File(path));
			
			int max = picCanvas.getMaxFrames();
			seekBar.setMax(max);
			Toast.makeText(this, max + " Duração us", Toast.LENGTH_LONG).show();
		
			
			picCanvas.startOrientationView();
			/*
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar par) {
				picCanvas.setFrame(seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
			});
			*/
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
