package pikachurun_network.v1;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import pikachurun.v2.Obstacle;

public class UserDisplay {
	
	int pikachuNumber;
	boolean isAlive;
	Image pikachuImg;
	int pikaY;
	int lineX;
	List<Obstacle> liveObstacleList = new ArrayList<>();
	
	public UserDisplay(int n, boolean isAlive, Image img, int y, List<Obstacle> list) {
		pikachuNumber = n;
		this.isAlive = isAlive;
		pikachuImg = img;
		pikaY = y;
		liveObstacleList = list;
	}
}
