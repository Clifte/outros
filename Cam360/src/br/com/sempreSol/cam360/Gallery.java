package br.com.sempreSol.cam360;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Gallery extends Activity {

	int licenseLimit = 9999;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		File[] files = listFiles();
		List<File> fileList = Arrays.asList(files);
		
		final ListView listview = (ListView) findViewById(R.id.listView_gallery);
		
	    final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1,android.R.id.text1, fileList);  listview.setAdapter(adapter);	
		
		
	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent =  new Intent(Gallery.this, PictureViewActivity.class);
				myIntent.putExtra("file",((File)listview.getItemAtPosition(position)).getAbsolutePath());
				startActivity(myIntent);
			}
		});
	}

	private File[] listFiles() {
		File mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"CAM360_SempreSol");

		return mediaStorageDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {	
					return filename.endsWith(".mp4") && new File(dir.getAbsolutePath() + "/" + filename + ".osd").exists();
				}
		});

	}
	
	
	
	
	
	
	
	private class StableArrayAdapter extends ArrayAdapter<File>{

		public StableArrayAdapter(Context context, int resource,
				int textViewResourceId, List<File> objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

	};	
	
	
}
