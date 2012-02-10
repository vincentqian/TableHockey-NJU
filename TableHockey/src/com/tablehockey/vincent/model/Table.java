package com.tablehockey.vincent.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Table extends Model{
	
	public Table(Bitmap bitmap){
		super(bitmap);
	}
	
	@Override
	public void draw(Canvas canvas){
		//canvas.drawBitmap(bitmap, 0, 0, null);
		Rect src = new Rect(0, 0, 1280, 800);
		Rect dst = new Rect(0, 0, 1280, 750);
		canvas.drawBitmap(bitmap, src, dst, null);
	}
}
