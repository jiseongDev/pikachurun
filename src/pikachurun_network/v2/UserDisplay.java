package pikachurun_network.v2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDisplay implements Serializable{
	
	int pikachuNumber;
	boolean isAlive;
	int pikaY;
	int lineX;
	List<Obstacle> liveObstacleList = new ArrayList<>();
	int imgNum;
	
	public UserDisplay(int n, boolean isAlive, int imgNum, int pikaY, List<Obstacle> list, int lineX) {
		pikachuNumber = n;
		this.isAlive = isAlive;
		this.imgNum = imgNum;
		this.pikaY = pikaY;
		liveObstacleList = list;
		this.lineX = lineX;
	}
}
