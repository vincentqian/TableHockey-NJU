package com.tablehockey.vincent.animations;

import com.tablehockey.vincent.R;
import com.tablehockey.vincent.view.PlayingGameView;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class SnowAnimation {
	
	private Context context;
	private Bitmap[] snowFlowers;
	private int[] redX,redY,blueX,blueY,randomSnow;
	private int[] diffY;
	private int redLoops,blueLoops;
	private boolean isPaused;
	
	public SnowAnimation(Context context){
		this.context = context;
		
		//initiate the snow flowers
		snowFlowers = new Bitmap[4];
		snowFlowers[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow1);
		snowFlowers[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow2);
		snowFlowers[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow3);
		snowFlowers[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow4);
		
		redX = new int[30];
		redY = new int[30];
		blueX = new int[30];
		blueY = new int[30];
		randomSnow = new int[30];
		isPaused = false;
		resetRedSnow();
		resetBlueSnow();
		diffY = new int[30];
		for(int i = 0; i < 30; i++){
			diffY[i] = (int) (Math.random()*4+4);
			randomSnow[i] = (int) (Math.random()*3);
		}
	}
	
	public void snowAtRedArea(Canvas canvas){
		if(redLoops < 70){
			for(int i = 0; i < 30; i++){
				canvas.drawBitmap(snowFlowers[randomSnow[i]], redX[i], redY[i], null);
				if(!isPaused){
					redY[i]+=diffY[i];
				}
			}
			if(!isPaused){
				redLoops++;
			}
		}else{
			PlayingGameView.redSnowing = false;
			resetRedSnow();
		}
	}
	
	public void snowAtBlueArea(Canvas canvas){
		if(blueLoops < 70){
			for(int i = 0; i < 30; i++){
				canvas.drawBitmap(snowFlowers[randomSnow[i]], blueX[i], blueY[i], null);
				if(!isPaused){
					blueY[i]+=diffY[i];
				}	
			}
			if(!isPaused){
				blueLoops++;
			}
		}else{
			PlayingGameView.blueSnowing = false;
			resetBlueSnow();
		}
	}
	
	private void resetRedSnow(){
		redLoops = 0;
		for(int i = 0; i < 30; i++){
			redX[i] = 660 + i*20;
			redY[i] = (int) (Math.random()*290+10);
		}
	}
	
	private void resetBlueSnow(){
		blueLoops = 0;
		for(int i = 0; i < 30; i++){
			blueX[i] = 40+20*i;
			blueY[i] = (int) (Math.random()*290+10);
		}
	}
	
	public void setPaused(boolean isPaused){
		this.isPaused = isPaused;
	}

}
