package pikachurun.v3;

import java.awt.Image;

public class Obstacle {
	private Image image;
	private int x;
	private int y;

	private int width;
	private int heigth;
	private int centerX;
	private int centerY;
	private int radius;

	public Obstacle(Image image, int x, int y, int width, int heigth, int centerX, int centerY, int radius) {
		this.image = image;
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

	public Image getImage() {
		return image;
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
