package com.tablehockey.vincent.view;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainGameThread extends Thread{
	
	private static final String TAG = MainGameThread.class.getSimpleName();
	
	private boolean running;
	
	private SurfaceHolder surfaceHolder;
	private PlayingGameView playingGameView;
	
	//desired fps
	private final static int MAX_FPS = 25;
	//maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	//the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	
	public MainGameThread(SurfaceHolder surfaceHolder, PlayingGameView playingGameView){
		super();
		this.surfaceHolder = surfaceHolder;
		this.playingGameView = playingGameView;
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}

	@Override
	public void run(){
		Canvas canvas;
		
		long beginTime;
		long timeDiff;		//the time it took for the circle to execute
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
					
					this.playingGameView.update();
					this.playingGameView.render(canvas);
					
					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int) (FRAME_PERIOD - timeDiff);
					
					if(sleepTime > 0){
						try{
							Thread.sleep(sleepTime);
						}catch(InterruptedException e){}
					}
					
					//control the game speed
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS){
						this.playingGameView.update();
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
