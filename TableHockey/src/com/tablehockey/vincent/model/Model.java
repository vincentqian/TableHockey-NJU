package com.tablehockey.vincent.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tablehockey.vincent.model.attr.Position;
import com.tablehockey.vincent.model.attr.Size;

public class Model {
	
	protected Position position;
	protected Size size;
	protected Bitmap bitmap;
	
	public Model(){
		position = new Position();
		size = new Size();
	}
	
	public Model(Bitmap bitmap){
		this.bitmap = bitmap;
		this.size = new Size();
		this.size.setWidth(bitmap.getWidth());
		this.size.setHeight(bitmap.getHeight());
	}
	
	public Model(Position position, Size size){
		this.position = position;
		this.size = size;
	}

	public Model(Position position, Size size, Bitmap bitmap){
		this.position = position;
		this.size = size;
		this.bitmap = bitmap;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public void draw(Canvas canvas){
		
	}
}
