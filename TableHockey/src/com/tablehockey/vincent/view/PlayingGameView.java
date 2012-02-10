package com.tablehockey.vincent.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.tablehockey.vincent.R;
import com.tablehockey.vincent.animations.SnowAnimation;
import com.tablehockey.vincent.model.MotherBall;
import com.tablehockey.vincent.model.RunningBall;
import com.tablehockey.vincent.model.Table;
import com.tablehockey.vincent.model.attr.Position;
import com.tablehockey.vincent.model.attr.Speed;
import com.tablehockey.vincent.music.MusicManager;

public class PlayingGameView extends SurfaceView implements SurfaceHolder.Callback{
	
	private static final String TAG = PlayingGameView.class.getSimpleName();
	
	//the variables of game states
	public final static int RUNNING = 1;
	public final static int START = 2;
	public final static int PAUSED = 3;
	public static int GAME_STATE;
	
	//the main thread to control the game loop
	private MainGameThread thread;
	
	//the manager responsible to play music
	private MusicManager musicManager;
	
	//Animations
	private SnowAnimation snowAnimation;
	
	//the table and the ball instances
	private Table table;
	private MotherBall redMotherBall;
	private MotherBall blueMotherBall;
	private RunningBall runningBall;
	
	//the temporary position of the ball
	private Position tmpRedPosition;
	private Position tmpBluePosition;
	
	//some bitmap resources
	private Bitmap btnStop;
	private Bitmap btnStart;
	private Bitmap twoDots;
	private Bitmap grayBitmap;
	private Bitmap snowBitmap;
	private Bitmap snowBg;
	private Bitmap arrow1;
	private Bitmap arrow2;
	private Bitmap toolsBox;
	
	//these variables are set to adjust the speed of running ball
	private int speed_scale = 3;
	private int friction_force = 1;
	private int bound_xv = 35;
	private int bound_yv = 35;
	
	//-----------------
	private boolean blueSnowable = true, redSnowable = true; 
	public static boolean blueSnowing = false, redSnowing = false;
	
	//---------------
	private boolean blueToolsVisible = false;
	private boolean redToolsVisible = false;
	
	//-------------
	private boolean redHitted = false;
	private boolean blueHitted = false;
	
	public PlayingGameView(Context context){
		super(context);
		getHolder().addCallback(this);
		
		//get the drawable resources
		table = new Table(BitmapFactory.decodeResource(getResources(), R.drawable.table));
		redMotherBall = new MotherBall(BitmapFactory.decodeResource(getResources(), R.drawable.redball));
		blueMotherBall = new MotherBall(BitmapFactory.decodeResource(getResources(), R.drawable.blueball));
		runningBall = new RunningBall(BitmapFactory.decodeResource(getResources(), R.drawable.ball));
		blueMotherBall.setPosition(new Position(320, 375));
		runningBall.setBluePosition();
		redMotherBall.setPosition(new Position(960, 375));
		btnStart = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
		btnStop = BitmapFactory.decodeResource(getResources(), R.drawable.stop_button);
		twoDots = BitmapFactory.decodeResource(getResources(), R.drawable.two_dots);
		grayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gray);
		snowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snow);
		snowBg = BitmapFactory.decodeResource(getResources(), R.drawable.snow_bg);
		arrow1 = BitmapFactory.decodeResource(getResources(), R.drawable.arrow1);
		arrow2 = BitmapFactory.decodeResource(getResources(), R.drawable.arrow2);
		toolsBox = BitmapFactory.decodeResource(getResources(), R.drawable.tools);
		
		//initiate the numbers
		MotherBall.numbers[0] = BitmapFactory.decodeResource(getResources(), R.drawable.number0);
		MotherBall.numbers[1] = BitmapFactory.decodeResource(getResources(), R.drawable.number1);
		MotherBall.numbers[2] = BitmapFactory.decodeResource(getResources(), R.drawable.number2);
		MotherBall.numbers[3] = BitmapFactory.decodeResource(getResources(), R.drawable.number3);
		MotherBall.numbers[4] = BitmapFactory.decodeResource(getResources(), R.drawable.number4);
		MotherBall.numbers[5] = BitmapFactory.decodeResource(getResources(), R.drawable.number5);
		MotherBall.numbers[6] = BitmapFactory.decodeResource(getResources(), R.drawable.number6);
		MotherBall.numbers[7] = BitmapFactory.decodeResource(getResources(), R.drawable.number7);
		MotherBall.numbers[8] = BitmapFactory.decodeResource(getResources(), R.drawable.number8);
		MotherBall.numbers[9] = BitmapFactory.decodeResource(getResources(), R.drawable.number9);
		
		//initiate the positions
		tmpRedPosition = new Position();
		tmpBluePosition = new Position();
		tmpRedPosition.setPos_x(redMotherBall.getPosition().getPos_x());
		tmpRedPosition.setPos_y(redMotherBall.getPosition().getPos_y());
		tmpBluePosition.setPos_x(blueMotherBall.getPosition().getPos_x());
		tmpBluePosition.setPos_y(blueMotherBall.getPosition().getPos_y());
		
		//set the active area
		int motherBallRadius = blueMotherBall.getSize().getWidth()/2;
		int runningBallRadius = runningBall.getSize().getWidth()/2;
		blueMotherBall.getActiveRect().set(22+motherBallRadius, 22+motherBallRadius, 636-motherBallRadius, 728-motherBallRadius);
		redMotherBall.getActiveRect().set(644+motherBallRadius, 22+motherBallRadius, 1258-motherBallRadius, 728-motherBallRadius);
		runningBall.getActiveRect().set(22+runningBallRadius,22+runningBallRadius,1258-runningBallRadius,728-runningBallRadius);
		
		//instant the thread and music manager
		thread = new MainGameThread(getHolder(), this);
		musicManager = MusicManager.getInstance(getContext());
		snowAnimation = new SnowAnimation(getContext());
		
		setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
		//start the game
		thread.setRunning(true);
		thread.start();
		//set the game state to START
		this.GAME_STATE = RUNNING;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while(retry){
			try{
				thread.join();
				retry = false;
			}catch (InterruptedException e) {
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		//get the action code and x, y
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		int pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		int x = (int) event.getX(pid);
		int y = (int) event.getY(pid);
		
		
		if(actionCode == MotionEvent.ACTION_DOWN || 
				actionCode == MotionEvent.ACTION_POINTER_DOWN){
			
			int r = redMotherBall.getSize().getWidth()/2;

			//pause or start the game
			if(x > 615 && x < 665 && y > 350 && y < 400){
				if(GAME_STATE == RUNNING){
					GAME_STATE = PAUSED;
					snowAnimation.setPaused(true);
				}else{
					GAME_STATE = RUNNING;
					snowAnimation.setPaused(false);
				}
			}
			
			//touch the tools boxes arrows
			if(x>0 && x<40 && y>0 && y<40){
				if(blueToolsVisible){
					blueToolsVisible = false;
				}else{
					blueToolsVisible = true;
				}
			}
			if(x>1240 && x<1280 && y>710 && y<750){
				if(redToolsVisible){
					redToolsVisible = false;
				}else{
					redToolsVisible = true;
				}
			}
			
			//whether snow animation is triggered
			if(blueSnowable && x>130 && x<170 && y>130 && y<170){
				blueSnowable = false;
				redSnowing = true;
			}
			if(redSnowable && x>1110 && x<1150 && y>580 && y<620){
				redSnowable = false;
				blueSnowing = true;
			}
			
			//whether redMotherBall is touched
			if(Math.abs(x-redMotherBall.getPosition().getPos_x())<r && 
					Math.abs(y-redMotherBall.getPosition().getPos_y())<r){
				redMotherBall.setTouched(true);
				redMotherBall.setPid(pid);
				Log.d(TAG, "********red ball touched....pid "+pid);
			}
			
			//whether blueMotherBall is touched
			if(Math.abs(x-blueMotherBall.getPosition().getPos_x())<r 
					&& Math.abs(y-blueMotherBall.getPosition().getPos_y())<r){
				blueMotherBall.setTouched(true);
				blueMotherBall.setPid(pid);
				Log.d(TAG, "********blue ball touched....pid "+pid);
			}
		}else if(GAME_STATE==RUNNING && event.getAction() == MotionEvent.ACTION_MOVE){
			
			int redPosX, redPosY, bluePosX, bluePosY;
			//update the redMotherBall
			if(redMotherBall.getPid() != -1){
				redPosX = (int)event.getX(redMotherBall.getPid());
				redPosY = (int)event.getY(redMotherBall.getPid());
				if(redPosX>=redMotherBall.getActiveRect().left && 
						redPosX<=redMotherBall.getActiveRect().right){
					redMotherBall.getPosition().setPos_x(redPosX);
				}
				if(redPosY>=redMotherBall.getActiveRect().top && 
						redPosY<=redMotherBall.getActiveRect().bottom){
					redMotherBall.getPosition().setPos_y(redPosY);
				}
			}
			
			//update the blueMotherBall
			if(blueMotherBall.getPid() != -1){
				bluePosX = (int)event.getX(blueMotherBall.getPid());
				bluePosY = (int)event.getY(blueMotherBall.getPid());
				if(bluePosX>=blueMotherBall.getActiveRect().left && 
						bluePosX<=blueMotherBall.getActiveRect().right){
					blueMotherBall.getPosition().setPos_x(bluePosX);
				}
				if(bluePosY>=redMotherBall.getActiveRect().top && 
						bluePosY<=redMotherBall.getActiveRect().bottom){
					blueMotherBall.getPosition().setPos_y(bluePosY);
				}
			}
			
			//update the running ball speed
			int redX = redMotherBall.getPosition().getPos_x();
			int redY = redMotherBall.getPosition().getPos_y();
			int blueX = blueMotherBall.getPosition().getPos_x();
			int blueY = blueMotherBall.getPosition().getPos_y();
			int runningX = runningBall.getPosition().getPos_x();
			int runningY = runningBall.getPosition().getPos_y();
			int l = redMotherBall.getSize().getWidth()/2 + runningBall.getSize().getWidth()/2;
			
			//if the red ball hit the running ball
			if(!redHitted && Math.abs(redX-runningX)<=l && Math.abs(redY-runningY)<=l){
				//set the running ball the speed of red ball
				runningBall.getSpeed().setXv(redMotherBall.getSpeed().getXv());
				runningBall.getSpeed().setYv(redMotherBall.getSpeed().getYv());
				runningBall.getSpeed().setxDirection(redMotherBall.getSpeed().getxDirection());
				runningBall.getSpeed().setyDirection(redMotherBall.getSpeed().getyDirection());
				/*try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				redHitted = true;
				musicManager.play(MusicManager.SOUND_KICK);
				renderAtOnce();
				//Log.d(TAG, "********set red ball speed"+redMotherBall.getSpeed().getXv());
			}else if(Math.abs(redX-runningX)>l || Math.abs(redY-runningY)>l){
				redHitted = false;
			}
			
			//if the blue ball hit the running ball
			if(!blueHitted && Math.abs(blueX-runningX)<=l && Math.abs(blueY-runningY)<=l){
				//set the running ball the speed of blue ball
				runningBall.getSpeed().setXv(blueMotherBall.getSpeed().getXv());
				runningBall.getSpeed().setYv(blueMotherBall.getSpeed().getYv());
				runningBall.getSpeed().setxDirection(blueMotherBall.getSpeed().getxDirection());
				runningBall.getSpeed().setyDirection(blueMotherBall.getSpeed().getyDirection());
				/*try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				blueHitted = true;
				musicManager.play(MusicManager.SOUND_KICK);
				renderAtOnce();
				//Log.d(TAG, "********set blue ball speed"+blueMotherBall.getSpeed().getYv());
			}else if(Math.abs(blueX-runningX)>l || Math.abs(blueY-runningY)>l){
				blueHitted = false;
			}
		}else if(actionCode == MotionEvent.ACTION_UP ||
				actionCode == MotionEvent.ACTION_POINTER_UP){
			
			//whether the point touched redMotherBall is up
			if(pid == redMotherBall.getPid()){
				redMotherBall.setTouched(false);
				redMotherBall.setPid(-1);
				Log.d(TAG, "********red ball up....pid "+pid);
			}//whether the point touched blueMotherBall is up
			else if(pid == blueMotherBall.getPid()){
				blueMotherBall.setTouched(false);
				blueMotherBall.setPid(-1);
				Log.d(TAG, "********blue ball up....pid "+pid);
			}
		}
		return true;
	}
	
	public void update(){
		
		if(GAME_STATE == PAUSED){
			return;
		}
		
		int blue_diff_x = (blueMotherBall.getPosition().getPos_x() - tmpBluePosition.getPos_x());
		int blue_diff_y = (blueMotherBall.getPosition().getPos_y() - tmpBluePosition.getPos_y());
		int red_diff_x = (redMotherBall.getPosition().getPos_x() - tmpRedPosition.getPos_x());
		int red_diff_y = (redMotherBall.getPosition().getPos_y() - tmpRedPosition.getPos_y());
		
		
		//update the temporary position
		tmpBluePosition.setPos_x(blueMotherBall.getPosition().getPos_x());
		tmpBluePosition.setPos_y(blueMotherBall.getPosition().getPos_y());
		tmpRedPosition.setPos_x(redMotherBall.getPosition().getPos_x());
		tmpRedPosition.setPos_y(redMotherBall.getPosition().getPos_y());
		
		int runningX = runningBall.getPosition().getPos_x();
		int runningY = runningBall.getPosition().getPos_y();
		
		
		//Log.d(TAG, "********X:"+runningX+"  Y:"+runningY);
		//if the running ball get into the hole
		if(runningX < 42 && runningY > 196 && runningY < 556){
			Log.d(TAG, "*******get into bluehole....");
			musicManager.play(MusicManager.SOUND_HOLE);
			redMotherBall.incrementScore();
			runningBall.setBluePosition();
			return ;
		}else if(runningX > 1238 && runningY > 196 && runningY < 556){
			Log.d(TAG, "********get into redhole....");
			musicManager.play(MusicManager.SOUND_HOLE);
			blueMotherBall.incrementScore();
			runningBall.setRedPosition();
			return;
		}
		
		//update the blue ball speed
		if(blue_diff_x > 0){
			blueMotherBall.getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
			blueMotherBall.getSpeed().setXv(blue_diff_x);
		}else if(blue_diff_x < 0){
			blueMotherBall.getSpeed().setxDirection(Speed.DIRECTION_LEFT);
			blueMotherBall.getSpeed().setXv(blue_diff_x*-1);
		}
		
		if(blue_diff_y > 0){
			blueMotherBall.getSpeed().setyDirection(Speed.DIRECTION_DOWN);
			blueMotherBall.getSpeed().setYv(blue_diff_y);
		}else if(blue_diff_y < 0){
			blueMotherBall.getSpeed().setyDirection(Speed.DIRECTION_UP);
			blueMotherBall.getSpeed().setYv(blue_diff_y*-1);
		}
		
		//update the red ball speed
		if(red_diff_x > 0){
			redMotherBall.getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
			redMotherBall.getSpeed().setXv(red_diff_x);
		}else if(red_diff_x < 0){
			redMotherBall.getSpeed().setxDirection(Speed.DIRECTION_LEFT);
			redMotherBall.getSpeed().setXv(red_diff_x*-1);
		}
		
		if(red_diff_y > 0){
			redMotherBall.getSpeed().setyDirection(Speed.DIRECTION_DOWN);
			redMotherBall.getSpeed().setYv(red_diff_y);
		}else if(red_diff_y < 0){
			redMotherBall.getSpeed().setyDirection(Speed.DIRECTION_UP);
			redMotherBall.getSpeed().setYv(red_diff_y*-1);
		}		
		
		
		
		//change the direction of running ball
		if(runningX<=runningBall.getActiveRect().left&&
				runningBall.getSpeed().getxDirection()==Speed.DIRECTION_LEFT){
			runningBall.getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
			musicManager.play(MusicManager.SOUND_PA2);
		}else if(runningX>=runningBall.getActiveRect().right&&
				runningBall.getSpeed().getxDirection()==Speed.DIRECTION_RIGHT){
			runningBall.getSpeed().setxDirection(Speed.DIRECTION_LEFT);
			musicManager.play(MusicManager.SOUND_PA2);
		}else if(runningY<=runningBall.getActiveRect().top&&
				runningBall.getSpeed().getyDirection()==Speed.DIRECTION_UP){
			runningBall.getSpeed().setyDirection(Speed.DIRECTION_DOWN);
			musicManager.play(MusicManager.SOUND_PA2);
		}else if(runningY>=runningBall.getActiveRect().bottom&&
				runningBall.getSpeed().getyDirection()==Speed.DIRECTION_DOWN){
			runningBall.getSpeed().setyDirection(Speed.DIRECTION_UP);
			musicManager.play(MusicManager.SOUND_PA2);
		}
		
		//update the running ball position
		Speed speed = runningBall.getSpeed();
		runningBall.getPosition().setPos_x(runningBall.getPosition().getPos_x()+speed.getXv()*speed.getxDirection());
		runningBall.getPosition().setPos_y(runningBall.getPosition().getPos_y()+speed.getYv()*speed.getyDirection());
		
		//minus the friction force
		int xv = speed.getXv();
		int yv = speed.getYv();
		if(xv > bound_xv)
			runningBall.getSpeed().setXv(xv-friction_force);
		if(yv > bound_yv)
			runningBall.getSpeed().setYv(yv-friction_force);
		
	}
	
	public void render(Canvas canvas){
		//draw background and table
		canvas.drawColor(Color.BLACK);
		table.draw(canvas);
		
		//draw snow
		if(blueSnowing){
			canvas.drawBitmap(snowBg, 22, 22, null);
			snowAnimation.snowAtBlueArea(canvas);
		}
		if(redSnowing){
			canvas.drawBitmap(snowBg, 644, 22, null);
			snowAnimation.snowAtRedArea(canvas);
		}
		
		//draw arrows,show icon, tools boxes
		if(!blueToolsVisible){
			canvas.drawBitmap(arrow1, 0, 0, null);
		}else{
			if(blueSnowable){
				canvas.drawBitmap(toolsBox, 22, 22, null);
				canvas.drawBitmap(snowBitmap, 130, 130, null);
			}else{
				canvas.drawBitmap(snowBitmap, 130, 130, null);
				canvas.drawBitmap(toolsBox, 22, 22, null);
			}
			canvas.drawBitmap(arrow2, 0, 0, null);
		}
				
		if(!redToolsVisible){
			canvas.drawBitmap(arrow2, 1240, 710, null);
			}else{
				if(redSnowable){
					canvas.drawBitmap(toolsBox, 1108, 578, null);
					canvas.drawBitmap(snowBitmap, 1110, 580, null);
				}else{
					canvas.drawBitmap(snowBitmap, 1110, 580, null);
					canvas.drawBitmap(toolsBox, 1108, 578, null);
				}
				
				canvas.drawBitmap(arrow1, 1240, 710, null);
		}
		
		//draw the stop/start button
		if(GAME_STATE == RUNNING){
			canvas.drawBitmap(btnStop, 615, 350, null);
		}else{
			canvas.drawBitmap(btnStart, 615, 350, null);
		}
		
		//draw scores
		redMotherBall.drawScore(canvas, MotherBall.FLAG_RED);
		blueMotherBall.drawScore(canvas, MotherBall.FLAG_BLUE);
		canvas.drawBitmap(twoDots, 628, 668, null);
		
		//draw three balls
		runningBall.draw(canvas);
		redMotherBall.draw(canvas);
		blueMotherBall.draw(canvas);
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
