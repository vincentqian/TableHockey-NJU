package com.tablehockey.vincent.model;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.SlidingDrawer;

public class MotherBall extends Ball{
	
	public static int FLAG_RED = 1;
	public static int FLAG_BLUE = 2;
	
	public static Bitmap[] numbers;
	
	private boolean isTouched;
	private int pid;			//the id of point which touch the ball
	private int score;
	
	public MotherBall(Bitmap bitmap){
		super(bitmap);
		isTouched = false;
		pid = -1;
		score = 0;
		numbers = new Bitmap[10];
	}

	public boolean isTouched() {
		return isTouched;
	}

	public void setTouched(boolean isTouched) {
		this.isTouched = isTouched;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public void incrementScore(){
		score++;
	}
	
	public void drawScore(Canvas canvas, int flag){
		String s = Integer.toString(score);
		if(flag == FLAG_RED){
			for(int i = 0; i < s.length(); i++){
				int index = s.charAt(i) - '0';
				canvas.drawBitmap(numbers[index], 652+i*45, 668, null);
			}
		}else{
			for(int i = s.length()-1; i >= 0; i--){
				int index = s.charAt(i) - '0';
				canvas.drawBitmap(numbers[index], 628-(s.length()-i)*45, 668, null);
			}
		}
	}
	
}
