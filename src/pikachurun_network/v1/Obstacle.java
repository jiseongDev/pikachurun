package pikachurun_network.v1;

import java.awt.Image;

public class Obstacle {
	private Image image;
	private int x;
	private int y;

	private int width;
	private int heigth;

	public Obstacle(Image image, int x, int y, int width, int heigth) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.width = width;
		this.heigth = heigth;
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
