package com.tablehockey.vincent;

import com.tablehockey.vincent.view.PlayingGameView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PlayingActivity extends Activity {
	
	private static final String TAG = PlayingActivity.class.getSimpleName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(new PlayingGameView(this));
        
        Log.d(TAG, "********View added");
    }
    
    @Override
	protected void onDestroy(){
		Log.d(TAG, "********Destroying...");
		super.onDestroy();
	}
	
	@Override
	protected void onStop(){
		Log.d(TAG, "********Stopping...");
		super.onStop();
	}
	
	@Override
	protected void onPause(){
		Log.d(TAG, "********onPause....");
		super.onPause();
		System.exit(0);
		/*Intent intent = new Intent();
		intent.setClass(PlayingActivity.this, GameMenuActivity.class);
		startActivity(intent);
		finish();*/
	}
}