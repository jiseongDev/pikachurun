package pikachurun_network.v2;

import java.io.Serializable;

public class Obstacle implements Serializable{
	private int imgNum;
	private int x;
	private int y;

	private int width;
	private int heigth;

	public Obstacle(int imgNum, int x, int y, int width, int heigth) {
		this.imgNum = imgNum;
		this.x = x;
		this.y = y;
		this.width = width;
		this.heigth = heigth;
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
