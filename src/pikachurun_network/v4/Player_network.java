package pikachurun_network.v4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Player_network extends JFrame {
	Socket socket;

	public JPanel canvas;
	private JPanel scorePanel;

	private String score;
	private long startTime;

	private JLabel scoreLabel;

	private Image pikachuReadyImg;

	private Image pikaRun1Img;
	private Image pikaRun2Img;
	private Image pikaRun3Img;
	private Image pikaRun4Img;

	private Image pikaRunBy2_1Img;
	private Image pikaRunBy2_2Img;

	private Image groObsImg1;
	private Image groObsImg2;

	private Image skyObsImg1;

	private Image jumpImage;
	private Image runImage;
	private Image nothing = new ImageIcon("src/images/nothing.png").getImage();

	private List<Image> pikaRunImgList = new ArrayList<>();
	private List<Obstacle> obstacleList = new ArrayList<>();
	private List<Obstacle> liveObstacleList = new ArrayList<>();
	private List<Obstacle> startObstacleList = new ArrayList<>();
	private List<int[]> groList = new ArrayList<>();
	private List<int[]> startGroList = new ArrayList<>();

	private boolean isJumping;
	private boolean isRunningBy4;
	private boolean isStarted;
	private boolean isAlive = true;

	Thread jump;

	Timer t;
	Timer runBy4;
	Timer runBy2;
	Timer moveObstacle;
	Timer manageObstacle;
	Timer moveGround;
	Timer manageGround;
	Timer endMessageTimer;

	int i;

	private int pikaY = 130;
	private int lineX = 80;

	private int width;

	private int playerNumber;
	private int myNumber;
	UserDisplay ud;
	UserDisplay ud1;
	UserDisplay ud2;
	UserDisplay ud3;
	UserDisplay ud4;

	List<UserDisplay> udList = new ArrayList<>();
	List<Image> obsList = new ArrayList<>();
	List<ScoreRecord> srList = new ArrayList<>();

	int imgNum = 0;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int screenWidth = (int) screenSize.getWidth();
	int screenHeight = (int) screenSize.getHeight();

	ObjectInputStream ois;
	ObjectOutputStream oos;
	ObjectInputStream fis;
	ObjectOutputStream fos;
	File rank;

	private Player_network() {
		setTitle("Main Frame2");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(550, 250, 600, 300);
		width = getWidth();
		rank = new File("src/pikachurun_network/v3/Rank.txt");
		initSocket();
		initReceiver();
		initComponent();
		initRepaint();
		initObstacle();
		initPikachuRun();
		initEvent();
		setVisible(true);
		initTimer();
	}

	private void initReceiver() {
		try {
			playerNumber = (Integer) ois.readObject();
			System.out.println(playerNumber);
			myNumber = (Integer) ois.readObject();
			System.out.println(myNumber);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		new Thread() {

			@Override
			public void run() {
				try {
					while (true) {
						ud = (UserDisplay) ois.readObject();
						synchronized (udList) {
							udList.set(ud.pikachuNumber - 1, ud);
						}
						canvas.repaint();
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	private void initSocket() {
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress("192.168.0.40", 5001));
			System.out.println("Linked With Server");
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void beforeStart() {
		ud1 = new UserDisplay(1, true, 4, pikaY, startObstacleList, 80, startGroList);
		ud2 = new UserDisplay(2, true, 4, pikaY, startObstacleList, 80, startGroList);
		ud3 = new UserDisplay(3, true, 4, pikaY, startObstacleList, 80, startGroList);
		ud4 = new UserDisplay(4, true, 4, pikaY, startObstacleList, 80, startGroList);

		udList.add(ud1);
		udList.add(ud2);
		udList.add(ud3);
		udList.add(ud4);
	}

	private void initRepaint() {
		Timer repaintTimer = new Timer(50, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.repaint();
			}
		});
		// repaintTimer.start();
	}

	private void printRank() throws ClassNotFoundException, IOException {
		endMessageTimer.stop();
		ScoreRecord tmp;
		fis = new ObjectInputStream(new FileInputStream(rank));
		srList = (List<ScoreRecord>) fis.readObject();
		fis.close();
		for (int j = 0; j < playerNumber - 1; j++) {
			for (int k = j + 1; k <= playerNumber - 1; k++) {
				if (srList.get(j).score < srList.get(k).score) {
					tmp = srList.get(j);
					srList.set(j,srList.get(k));
					srList.set(k, tmp);
				}
			}
		}
		String player1 = null;
		String player2 = null;
		if(srList.get(0).pikachuNumber == myNumber) {
			player1 = "나";
			player2 = "상대";
		}else {
			player2 = "나";
			player1 = "상대";
		}
		String str = String.format("%s : %d점\n%s : %d점", player1, srList.get(0).score, player2, srList.get(1).score);
		JOptionPane.showMessageDialog(getContentPane(), str, "Congratulations!", JOptionPane.PLAIN_MESSAGE);
	}

	private void endMessage() {
		t.stop();
		runBy4.stop();
		runBy2.stop();
		moveObstacle.stop();
		manageObstacle.stop();
		moveGround.stop();
		manageGround.stop();

		endMessageTimer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int dieNum = 0;
				try {
					for (int i1 = 0; i1 < playerNumber; i1++) {
						if (udList.get(i1).isAlive == false) {
							dieNum++;
						}
					}
					if (dieNum == playerNumber) {
						Thread.sleep(100);
						printRank();
					}
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		});
		endMessageTimer.start();
	}

	private void initTimer() {
		t = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myDisplayWriter();
				scoreLabel.setText(score = String.format("%05d", (int) (System.currentTimeMillis() - startTime) / 10));
				if (isRunningBy4) {
					for (int i1 = 0; i1 < liveObstacleList.size(); i1++) {
						if (liveObstacleList.get(i1).getX() < 110
								&& liveObstacleList.get(i1).getX() + liveObstacleList.get(i1).getWidth() > 20) {
							if (liveObstacleList.get(i1).getY() + liveObstacleList.get(i1).getHeigth() >= 180) {
								isAlive = false;
								myDisplayWriter();
								try {
									fis = new ObjectInputStream(new FileInputStream(rank));
									Object obj = fis.readObject();
									srList = (List<ScoreRecord>) obj;
									fis.close();
									System.out.println(srList);
									srList.add(new ScoreRecord(myNumber,
											(int) (System.currentTimeMillis() - startTime) / 10));
									fos = new ObjectOutputStream(new FileOutputStream(rank));
									fos.writeObject(srList);
									fos.reset();
								} catch (IOException | ClassNotFoundException e1) {
									e1.printStackTrace();
								}
								JOptionPane.showInternalMessageDialog(getContentPane(), "Your Score is " + score);
								endMessage();
							}
						}
					}
				} else if (isJumping) {
					for (int i1 = 0; i1 < liveObstacleList.size(); i1++) {
						if (liveObstacleList.get(i1).getX() < 70
								&& liveObstacleList.get(i1).getX() + liveObstacleList.get(i1).getWidth() > 20) {
							if (Math.pow(liveObstacleList.get(i1).getY() + liveObstacleList.get(i1).getCenterY()
									- (pikaY + 37), 2)
									+ Math.pow(liveObstacleList.get(i1).getX() + liveObstacleList.get(i1).getCenterX()
											- 42, 2) < Math.pow(liveObstacleList.get(i1).getRadius() + 22, 2)) {
								jump.interrupt();
								isAlive = false;
								myDisplayWriter();
								try {
									fis = new ObjectInputStream(new FileInputStream(rank));
									Object obj = fis.readObject();
									srList = (List<ScoreRecord>) obj;
									fis.close();
									System.out.println(srList);
									srList.add(new ScoreRecord(myNumber,
											(int) (System.currentTimeMillis() - startTime) / 10));
									fos = new ObjectOutputStream(new FileOutputStream(rank));
									fos.writeObject(srList);
									fos.reset();
								} catch (IOException | ClassNotFoundException e1) {
									e1.printStackTrace();
								}
								JOptionPane.showInternalMessageDialog(getContentPane(), "Your Score is " + score);
								endMessage();
							}
						}
					}
				} else {
					for (int i1 = 0; i1 < liveObstacleList.size(); i1++) {
						if (liveObstacleList.get(i1).getX() < 60
								&& liveObstacleList.get(i1).getX() + liveObstacleList.get(i1).getWidth() > 20) {
							if (Math.pow(liveObstacleList.get(i1).getY() + liveObstacleList.get(i1).getCenterY()
									- (pikaY + 37), 2)
									+ Math.pow(liveObstacleList.get(i1).getX() + liveObstacleList.get(i1).getCenterX()
											- 42, 2) < Math.pow(liveObstacleList.get(i1).getRadius() + 22, 2)) {
								jump.interrupt();
								isAlive = false;
								myDisplayWriter();
								try {
									fis = new ObjectInputStream(new FileInputStream(rank));
									Object obj = fis.readObject();
									srList = (List<ScoreRecord>) obj;
									fis.close();
									System.out.println(srList);
									srList.add(new ScoreRecord(myNumber,
											(int) (System.currentTimeMillis() - startTime) / 10));
									fos = new ObjectOutputStream(new FileOutputStream(rank));
									fos.writeObject(srList);
									fos.reset();
								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (ClassNotFoundException e1) {
									e1.printStackTrace();
								}
								JOptionPane.showInternalMessageDialog(getContentPane(), "Your Score is " + score);
								endMessage();
							}
						}
					}
				}
			}
		});
	}

	private void myDisplayWriter() {
		try {
			UserDisplay myDisplay = new UserDisplay(myNumber, isAlive, imgNum, pikaY, liveObstacleList, lineX, groList);
			oos.writeObject(myDisplay);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initEvent() {
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int k = e.getKeyCode();
				if (k == KeyEvent.VK_UP) {
					if (!isStarted) {
						pikachuReadyImg = nothing;
						t.start();
						startTime = System.currentTimeMillis();
						runBy2.start();
						isStarted = true;
						extendLine();
					}
					if (isJumping || isRunningBy4)
						return;
					isJumping = true;
					jump = new Thread() {

						@Override
						public void run() {
							runImage = nothing;
							runBy2.stop();
							jumpImage = pikaRunBy2_1Img;
							imgNum = 4;
							long startTime = System.currentTimeMillis();
							long jumpTime = 0;
							while (true) {
								jumpTime = System.currentTimeMillis() - startTime;
								if (jumpTime > 600)
									break;
								pikaY = -(int) ((Math.pow((jumpTime - 300), 2) / -685));
							}
							isJumping = false;
							runBy2.restart();
						}

					};
					jump.start();
				} else if (k == KeyEvent.VK_DOWN) {
					if (!isRunningBy4 && !isJumping) {
						jumpImage = nothing;
						runImage = nothing;
						runBy2.stop();
						runBy4.restart();
						isRunningBy4 = true;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (!isJumping) {
						runBy4.stop();
						runBy2.restart();
						isRunningBy4 = false;
					}
				}
			}

		});
	}

	private void initPikachuRun() {
		runBy4 = new Timer(70, new ActionListener() {
			int i = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (i == 4)
					i = 0;
				imgNum = i;
				runImage = pikaRunImgList.get(i++);
			}
		});
		runBy4.start();
		runBy4.stop();

		runBy2 = new Timer(70, new ActionListener() {
			int i = 4;

			@Override
			public void actionPerformed(ActionEvent e) {
				jumpImage = nothing;
				if (i == 6)
					i = 4;
				imgNum = i;
				runImage = pikaRunImgList.get(i++);
			}

		});

	}

	private void initObstacle() {
		groObsImg1 = new ImageIcon("src/images/groundObstacle_잠만보.png").getImage();
		groObsImg2 = new ImageIcon("src/images/groundObstacle_이브이.png").getImage();

		obsList.add(groObsImg1);
		obsList.add(groObsImg2);

		obstacleList.add(new Obstacle(0, width, pikaY, groObsImg1.getWidth(this), groObsImg1.getHeight(this),
				groObsImg1.getWidth(this) / 2, groObsImg1.getHeight(this), groObsImg1.getHeight(this)));
		obstacleList.add(new Obstacle(1, width, pikaY, groObsImg2.getWidth(this), groObsImg2.getHeight(this),
				groObsImg2.getWidth(this) / 2, groObsImg2.getHeight(this) / 2, groObsImg2.getHeight(this) / 2));

		obstacleList.add(new Obstacle(0, width, pikaY, groObsImg1.getWidth(this), groObsImg1.getHeight(this),
				groObsImg1.getWidth(this) / 2, groObsImg1.getHeight(this), groObsImg1.getHeight(this)));
		obstacleList.add(new Obstacle(1, width, pikaY, groObsImg2.getWidth(this), groObsImg2.getHeight(this),
				groObsImg2.getWidth(this) / 2, groObsImg2.getHeight(this) / 2, groObsImg2.getHeight(this) / 2));

		skyObsImg1 = new ImageIcon("src/images/skyObstacle_뭐야이건.png").getImage();

		obsList.add(skyObsImg1);

		obstacleList.add(new Obstacle(2, width, pikaY - 50, skyObsImg1.getWidth(this), skyObsImg1.getHeight(this),
				skyObsImg1.getWidth(this) / 2, skyObsImg1.getHeight(this) / 2 - 5, 50));
		obstacleList.add(new Obstacle(2, width, pikaY - 90, skyObsImg1.getWidth(this), skyObsImg1.getHeight(this),
				skyObsImg1.getWidth(this) / 2, skyObsImg1.getHeight(this) / 2 - 5, 50));
		obstacleList.add(new Obstacle(2, width, pikaY - 10, skyObsImg1.getWidth(this), skyObsImg1.getHeight(this),
				skyObsImg1.getWidth(this) / 2, skyObsImg1.getHeight(this) / 2 - 5, 50));

		moveObstacle();
		manageObstacle();
		moveGround();
		manageGround();
	}

	private void initComponent() {

		scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		scoreLabel = new JLabel("00000");
		scorePanel.add(scoreLabel);

		pikaRun1Img = new ImageIcon("src/images/pikachuRunBy4_1.png").getImage();
		pikaRun2Img = new ImageIcon("src/images/pikachuRunBy4_2.png").getImage();
		pikaRun3Img = new ImageIcon("src/images/pikachuRunBy4_3.png").getImage();
		pikaRun4Img = new ImageIcon("src/images/pikachuRunBy4_4.png").getImage();

		pikaRunBy2_1Img = new ImageIcon("src/images/pikachuRunBy2_1.png").getImage();
		pikaRunBy2_2Img = new ImageIcon("src/images/pikachuRunBy2_2.png").getImage();

		jumpImage = nothing;
		runImage = nothing;
		pikachuReadyImg = pikaRunBy2_1Img;

		pikaRunImgList.add(pikaRun4Img);
		pikaRunImgList.add(pikaRun1Img);
		pikaRunImgList.add(pikaRun2Img);
		pikaRunImgList.add(pikaRun3Img);

		pikaRunImgList.add(pikaRunBy2_1Img);
		pikaRunImgList.add(pikaRunBy2_2Img);

		beforeStart();

		int panelHeight = 210 * playerNumber + 70;

		setBounds((screenWidth - 600) / 2, (screenHeight - panelHeight) / 2, 600, panelHeight);
		canvas = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				synchronized (udList) {
					for (UserDisplay tmpud : udList) {
						int n = tmpud.pikachuNumber - 1;
						g.drawLine(10, 200 + 200 * n, tmpud.lineX, 200 + 200 * n);
						for (Obstacle obs : tmpud.liveObstacleList) {
							g.drawImage(obsList.get(obs.getImgNum()), obs.getX(), obs.getY() + 200 * n, this);
						}
						for (int i = 0; i < tmpud.groList.size(); i++) {
							g.drawLine(tmpud.groList.get(i)[0], tmpud.groList.get(i)[2] + 200 * n,
									tmpud.groList.get(i)[1], tmpud.groList.get(i)[2] + 200 * n);
						}
						g.drawImage(pikaRunImgList.get(tmpud.imgNum), 20, tmpud.pikaY + 200 * n, this);
					}
				}
			}

		};

		canvas.setBackground(Color.WHITE);
		add(canvas);
		add(scorePanel, BorderLayout.NORTH);

	}

	private void extendLine() {
		new Thread() {

			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (true) {
					elapsedTime = System.currentTimeMillis() - startTime;
					if (elapsedTime > 600)
						break;
					lineX = (int) ((canvas.getWidth() - 20) * elapsedTime / 600);
				}
				manageObstacle.start();
				moveObstacle.start();
				manageGround.start();
				moveGround.start();
			}

		}.start();
	}

	private void manageObstacle() {
		new Thread() {

			@Override
			public void run() {
				manageObstacle = new Timer(1000, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						for (Iterator<Obstacle> iterator = liveObstacleList.iterator(); iterator.hasNext();) {
							Obstacle obs = iterator.next();
							if (obs.getX() < -200) {
								iterator.remove();
							}
						}
						if (Math.random() > 0.35) {
							Obstacle obs = obstacleList.get((int) (Math.random() * obstacleList.size()));
							Obstacle obsClone = new Obstacle(obs.getImgNum(), obs.getX(), obs.getY(), obs.getWidth(),
									obs.getHeigth(), obs.getCenterX(), obs.getCenterY(), obs.getRadius());
							liveObstacleList.add(obsClone);
						}

					}

				});
			}

		}.start();
	}

	private void moveObstacle() {
		new Thread() {

			@Override
			public void run() {
				moveObstacle = new Timer(10, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						for (Obstacle obs : liveObstacleList) {
							obs.setX(obs.getX() - 10);
						}
					}
				});
			}

		}.start();
	}

	private void manageGround() {
		new Thread() {

			@Override
			public void run() {
				manageGround = new Timer(30, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (i == 10) {
							for (Iterator<int[]> iterator = groList.iterator(); iterator.hasNext();) {
								int[] i = iterator.next();
								if (i[1] < -10) {
									iterator.remove();
								}
							}
							i = 0;
						}
						if (Math.random() > 0.7) {
							int x2 = 1;
							if (Math.random() < 0.3) {
								x2 = 2;
							} else if (Math.random() < 0.4) {
								x2 = 3;
							} else if (Math.random() < 0.5) {
								x2 = 4;
							}
							int[] gro = { 600, 600 + x2, 203 + (int) (Math.random() * 8) };
							groList.add(gro);
						}
						i++;
					}

				});
			}

		}.start();
	}

	private void moveGround() {
		new Thread() {

			@Override
			public void run() {
				moveGround = new Timer(5, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < groList.size(); i++) {
							groList.get(i)[0] -= 5;
							groList.get(i)[1] -= 5;
						}
					}
				});
			}

		}.start();
	}

	public static void main(String[] args) {
		new Player_network();
	}

}
