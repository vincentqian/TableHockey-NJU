package com.tablehockey.vincent.model.attr;

public class Speed {
	
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_LEFT = -1;
	public static final int DIRECTION_UP = -1;
	public static final int DIRECTION_DOWN = 1;
	
	private int xv = 1;	//velocity value on the X axis
	private int yv = 1;	//velocity value on the Y axis
	
	private int xDirection = DIRECTION_RIGHT;
	private int yDirection = DIRECTION_DOWN;
	
	public Speed(){
		this.xv = 0;
		this.yv = 0;
	}
	
	public Speed(int xv, int yv){
		this.xv = xv;
		this.yv = yv;
	}

	public int getXv() {
		return xv;
	}

	public void setXv(int xv) {
		this.xv = xv;
	}

	public int getYv() {
		return yv;
	}

	public void setYv(int yv) {
		this.yv = yv;
	}

	public int getxDirection() {
		return xDirection;
	}

	public void setxDirection(int xDirection) {
		this.xDirection = xDirection;
	}

	public int getyDirection() {
		return yDirection;
	}

	public void setyDirection(int yDirection) {
		this.yDirection = yDirection;
	}
	
	//change the direction on the X axis
	public void toggleXDirection(){
		xDirection *= -1;
	}
	
	//change the direction on the Y axis
	public void toggleYDirection(){
		yDirection *= -1;
	}
}
