package pikachurun_network.v4;

import java.io.Serializable;

public class Obstacle implements Serializable{
	private int imgNum;
	private int x;
	private int y;

	private int width;
	private int heigth;
	private int centerX;
	private int centerY;
	private int radius;

	public Obstacle(int imgNum, int x, int y, int width, int heigth, int centerX, int centerY, int radius) {
		this.imgNum = imgNum;
		this.x = x;
		this.y = y;
		this.width = width;
		this.heigth = heigth;
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public int getRadius() {
		return radius;
	}

	public int getImgNum() {
		return imgNum;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeigth() {
		return heigth;
	}

}
