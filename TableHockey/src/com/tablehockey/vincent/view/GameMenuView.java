package com.tablehockey.vincent.view;

import com.tablehockey.vincent.GameMenuActivity;
import com.tablehockey.vincent.PlayingActivity;
import com.tablehockey.vincent.R;
import com.tablehockey.vincent.model.MotherBall;
import com.tablehockey.vincent.model.RunningBall;
import com.tablehockey.vincent.model.Table;
import com.tablehockey.vincent.model.attr.Position;
import com.tablehockey.vincent.model.attr.Speed;
import com.tablehockey.vincent.music.MusicManager;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameMenuView extends SurfaceView implements SurfaceHolder.Callback {

	private final static String TAG = GameMenuView.class.getSimpleName();
	
	private GameMenuThread thread;
	
	private MusicManager musicManager;
	
	//drawable resources
	private Table table;
	private RunningBall runningBall;
	private MotherBall redBall;
	private Bitmap startBitmap;
	private Bitmap settingsBitmap;
	private Bitmap exitBitmap;
	
	//temporary red ball position
	private int tmpRedX, tmpRedY;
	
	//---------------
	private boolean redHitted = false;
	
	public GameMenuView(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		
		table = new Table(BitmapFactory.decodeResource(getResources(), R.drawable.table2));
		runningBall = new RunningBall(BitmapFactory.decodeResource(getResources(), R.drawable.ball));
		redBall = new MotherBall(BitmapFactory.decodeResource(getResources(), R.drawable.redball));
		startBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start);
		settingsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings);
		exitBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.exit);
		
		//set the positions
		runningBall.setPosition(new Position(640,375));
		redBall.setPosition(new Position(640, 700));
		
		//initiate the temporary position
		tmpRedX = redBall.getPosition().getPos_x();
		tmpRedY = redBall.getPosition().getPos_y();
		
		//set the active area
		int redBallRadius = redBall.getSize().getWidth()/2;
		int runningBallRadius = runningBall.getSize().getWidth()/2;
		redBall.getActiveRect().set(22+redBallRadius, 22+redBallRadius, 1258-redBallRadius, 728-redBallRadius);
		runningBall.getActiveRect().set(22+runningBallRadius, 22+runningBallRadius, 1258-runningBallRadius, 728-runningBallRadius);
		
		thread = new GameMenuThread(getHolder(), this);
		musicManager = MusicManager.getInstance(getContext());
		
		setFocusableInTouchMode(true);
		setFocusable(true);
		Log.d(TAG, "********CreateGameMenuView");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
		setFocusableInTouchMode(true);
		setFocusable(true);
		
		if(thread.getState() == Thread.State.TERMINATED){
			thread = new GameMenuThread(getHolder(), this);
		}
		thread.setRunning(true);
		thread.start();
		Log.d(TAG, "********surfaceCreated....");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
		boolean retry = true;
		while(retry){
			try{
				thread.join();
				retry = false;
			}catch (InterruptedException e) {
				Log.d(TAG, "********"+e.toString());
			}
		}
		Log.d(TAG, "********surfaceDestroyed....");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		Log.d(TAG, "********onTouchEvent....");
		
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		int pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		if(actionCode == MotionEvent.ACTION_DOWN ||
				actionCode == MotionEvent.ACTION_POINTER_DOWN){
			Log.d(TAG, "********ACTION_DOWN....");
			int r = redBall.getSize().getWidth()/2;
			
			if(Math.abs(x-redBall.getPosition().getPos_x()) < r &&
					Math.abs(y-redBall.getPosition().getPos_y())<r){
				redBall.setTouched(true);
				redBall.setPid(pid);
			}
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			//update the red ball position
			if(redBall.getPid() != -1){
				int redX = (int)event.getX(redBall.getPid());
				int redY = (int)event.getY(redBall.getPid());
				if(redX >= redBall.getActiveRect().left &&
						redX <= redBall.getActiveRect().right){
					redBall.getPosition().setPos_x(redX);
				}
				if(redY <= redBall.getActiveRect().bottom &&
						redY >= redBall.getActiveRect().top){
					redBall.getPosition().setPos_y(redY);
				}
				
				int runningX = runningBall.getPosition().getPos_x();
				int runningY = runningBall.getPosition().getPos_y();
				int l = redBall.getSize().getWidth()/2 + runningBall.getSize().getWidth()/2;
				//if the red ball hit the running ball
				if(!redHitted && Math.abs(redX - runningX)<=l && Math.abs(redY - runningY)<=l){
					
					update();
					
					runningBall.getSpeed().setxDirection(redBall.getSpeed().getxDirection());
					runningBall.getSpeed().setyDirection(redBall.getSpeed().getyDirection());
					runningBall.getSpeed().setXv(redBall.getSpeed().getXv());
					runningBall.getSpeed().setYv(redBall.getSpeed().getYv());
					
					redHitted = true;
					musicManager.play(MusicManager.SOUND_KICK);
					renderAtOnce();
					//to prevent this event from happening recurrently
					//update();
					/*try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
				}else if(Math.abs(redX - runningX) > l || Math.abs(redY - runningY)>l){
					redHitted = false;
				}
			}
			
			
		}else if(actionCode == MotionEvent.ACTION_UP || 
				actionCode == MotionEvent.ACTION_POINTER_UP){
			Log.d(TAG, "********ACTION_UP");
			if(pid == redBall.getPid()){
				redBall.setTouched(false);
				redBall.setPid(-1);
			}
		}
		return true;
	}

	public void update(){
		
		int red_diff_x = (redBall.getPosition().getPos_x() - tmpRedX)/3;
		int red_diff_y = (redBall.getPosition().getPos_y() - tmpRedY)/3;
		
		if(red_diff_x > 0){
			redBall.getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
			redBall.getSpeed().setXv(red_diff_x);
		}else if(red_diff_x < 0){
			redBall.getSpeed().setxDirection(Speed.DIRECTION_LEFT);
			redBall.getSpeed().setXv(red_diff_x * -1);
		}
		
		if(red_diff_y > 0){
			redBall.getSpeed().setyDirection(Speed.DIRECTION_DOWN);
			redBall.getSpeed().setYv(red_diff_y);
		}else if(red_diff_y < 0){
			redBall.getSpeed().setyDirection(Speed.DIRECTION_UP);
			redBall.getSpeed().setYv(red_diff_y * -1);
		}
		
		//judge if the running ball fall into the hole
		int runningX = runningBall.getPosition().getPos_x();
		int runningY = runningBall.getPosition().getPos_y();
		int topBound = runningBall.getActiveRect().top;
		
		if(runningX > 60 && runningX < 420 && runningY < topBound){
			musicManager.play(MusicManager.SOUND_HOLE);
			thread.setRunning(false);
			reset();
			//start the game
			Intent intent = new Intent();
			intent.setClass(getContext(), PlayingActivity.class);
			((Activity)getContext()).startActivity(intent);
			//((Activity)getContext()).setContentView(new PlayingGameView(getContext()));
			Log.d(TAG, "********start...");
		}else if(runningX > 460 && runningX < 820 && runningY < topBound){
			musicManager.play(MusicManager.SOUND_HOLE);
			Log.d(TAG, "********settings...");
		}else if(runningX > 860 && runningX < 1220 && runningY < topBound){
			musicManager.play(MusicManager.SOUND_HOLE);
			Log.d(TAG, "********exit...");
		}
		
		//change the direction of running ball
		if(runningBall.getPosition().getPos_x()<=runningBall.getActiveRect().left&&
				runningBall.getSpeed().getxDirection()==Speed.DIRECTION_LEFT){
			musicManager.play(MusicManager.SOUND_PA2);
			runningBall.getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
		}else if(runningBall.getPosition().getPos_x()>=runningBall.getActiveRect().right&&
				runningBall.getSpeed().getxDirection()==Speed.DIRECTION_RIGHT){
			musicManager.play(MusicManager.SOUND_PA2);
			runningBall.getSpeed().setxDirection(Speed.DIRECTION_LEFT);
				
		}
				
		if(runningBall.getPosition().getPos_y()<=runningBall.getActiveRect().top&&
				runningBall.getSpeed().getyDirection()==Speed.DIRECTION_UP){
			musicManager.play(MusicManager.SOUND_PA2);
			runningBall.getSpeed().setyDirection(Speed.DIRECTION_DOWN);
				
		}else if(runningBall.getPosition().getPos_y()>=runningBall.getActiveRect().bottom&&
				runningBall.getSpeed().getyDirection()==Speed.DIRECTION_DOWN){
			musicManager.play(MusicManager.SOUND_PA2);
			runningBall.getSpeed().setyDirection(Speed.DIRECTION_UP);
		}
		
		//update the running ball position
		Speed speed = runningBall.getSpeed();
		runningBall.getPosition().setPos_x(runningBall.getPosition().getPos_x()+speed.getXv()*speed.getxDirection());
		runningBall.getPosition().setPos_y(runningBall.getPosition().getPos_y()+speed.getYv()*speed.getyDirection());
		
		tmpRedX = redBall.getPosition().getPos_x();
		tmpRedY = redBall.getPosition().getPos_y();
	}
	
	public void render(Canvas canvas){
		//Log.d(TAG, "rendering...");
		canvas.drawColor(Color.BLACK);
		table.draw(canvas);
		canvas.drawBitmap(startBitmap, 140, 80, null);
		canvas.drawBitmap(settingsBitmap, 490, 80, null);
		canvas.drawBitmap(exitBitmap, 970, 80, null);
		runningBall.draw(canvas);
		redBall.draw(canvas);
		
	}
	
	private void reset(){
		//set the positions
		runningBall.setPosition(new Position(640,375));
		redBall.setPosition(new Position(640, 700));
				
		//initiate the temporary position
		tmpRedX = redBall.getPosition().getPos_x();
		tmpRedY = redBall.getPosition().getPos_y();
		
		//clear the speed
		runningBall.getSpeed().setXv(0);
		runningBall.getSpeed().setYv(0);
		redBall.getSpeed().setXv(0);
		redBall.getSpeed().setYv(0);
		
		redBall.setTouched(false);
		redBall.setPid(-1);
	}
	
	private void renderAtOnce(){
		update();
		Canvas canvas = getHolder().lockCanvas();
		synchronized (canvas) {
			render(canvas);
		}
		if(canvas != null){
			getHolder().unlockCanvasAndPost(canvas);
		}
	}
}
