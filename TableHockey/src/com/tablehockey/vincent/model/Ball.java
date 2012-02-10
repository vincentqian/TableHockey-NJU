package com.tablehockey.vincent.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.tablehockey.vincent.model.attr.Position;
import com.tablehockey.vincent.model.attr.Size;
import com.tablehockey.vincent.model.attr.Speed;

public class Ball extends Model{
	
	private Speed speed;
	private Rect activeRect;
	
	public Ball(){
		speed = new Speed();
		activeRect = new Rect();
	}
	
	public Ball(Bitmap bitmap){
		super(bitmap);
		speed = new Speed();
		activeRect = new Rect();
		position = new Position();
	}
	
	public Ball(Position position, Size size){
		super(position, size);
	}
	
	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	
	public Rect getActiveRect() {
		return activeRect;
	}

	public void setActiveRect(Rect activeRect) {
		this.activeRect = activeRect;
	}

	@Override
	public void draw(Canvas canvas){
		canvas.drawBitmap(bitmap, position.getPos_x()-size.getWidth()/2, position.getPos_y()-size.getHeight()/2, null);
	}
}
