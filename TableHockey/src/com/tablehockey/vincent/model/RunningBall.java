package com.tablehockey.vincent.model;

import android.graphics.Bitmap;

public class RunningBall extends Ball{
	
	public RunningBall(Bitmap bitmap){
		super(bitmap);
	}
	
	public void setRedPosition(){
		this.position.setPos_x(844);
		this.position.setPos_y(375);
		clearSpeed();
	}
	
	public void setBluePosition(){
		this.position.setPos_x(436);
		this.position.setPos_y(375);
		clearSpeed();
	}
	
	private void clearSpeed(){
		this.getSpeed().setXv(0);
		this.getSpeed().setYv(0);
	}

}
