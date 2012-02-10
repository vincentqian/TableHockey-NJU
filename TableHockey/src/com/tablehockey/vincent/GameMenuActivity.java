package com.tablehockey.vincent;

import com.tablehockey.vincent.view.GameMenuView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GameMenuActivity extends Activity{

	private static final String TAG = GameMenuActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(new GameMenuView(this));
		Log.d(TAG,"********View added");
	}
	
	@Override
	protected void onDestroy(){
		Log.d(TAG, "********Destroying...");
		super.onDestroy();
		System.exit(0);
	}
	
	@Override
	protected void onStop(){
		Log.d(TAG, "********Stopping...");
		super.onStop();
	}
}
