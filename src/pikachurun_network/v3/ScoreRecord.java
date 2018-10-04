package pikachurun_network.v3;

import java.io.Serializable;

public class ScoreRecord implements Serializable {
	int pikachuNumber;
	int score;
	public ScoreRecord(int pikachuNumber, int score) {
		super();
		this.pikachuNumber = pikachuNumber;
		this.score = score;
	}
}
