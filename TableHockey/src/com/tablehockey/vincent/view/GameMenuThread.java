package com.tablehockey.vincent.view;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameMenuThread extends Thread{
	
	private static final String TAG = GameMenuThread.class.getSimpleName();
	
	private boolean running;
	
	private SurfaceHolder surfaceHolder;
	private GameMenuView gameMenuView;
	
	//desired fps
	private final static int MAX_FPS = 25;
	//maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	//the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	
	public GameMenuThread(SurfaceHolder surfaceHolder, GameMenuView gameMenuView){
		super();
		this.surfaceHolder = surfaceHolder;
		this.gameMenuView = gameMenuView;
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}
	
	@Override
	public void run(){
		Canvas canvas;
		
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
		
		sleepTime = 0;
		
		while(running){
			canvas = null;
			try{
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					
					this.gameMenuView.update();
					this.gameMenuView.render(canvas);
					
					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if(sleepTime > 0){
						try{
							Thread.sleep(sleepTime);
						}catch(InterruptedException e){}
					}
					
					//control the game speed
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS){
						this.gameMenuView.update();
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			}finally{
				if(canvas != null){
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
}
