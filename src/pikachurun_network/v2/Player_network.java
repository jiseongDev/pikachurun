package pikachurun_network.v2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

	private int pikaY = 150;
	private int lineX = 80;

	private int width;

	private int playerNumber;
	private int myNumber;
	UserDisplay ud;
	UserDisplay ud1;
	UserDisplay ud2;
	
	Image ud1Img;
	Image ud2Img;
	
	List<UserDisplay> udList = new ArrayList<>();
	List<Image> obsList = new ArrayList<>();
	
	int imgNum = 0;

	ObjectInputStream ois;
	ObjectOutputStream oos;

	private Player_network() {
		setTitle("Main Frame2");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(550, 250, 600, 300);
		width = getWidth();
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
							udList.set(ud.pikachuNumber-1, ud);
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
		ud1 = new UserDisplay(1, true, 4, 150, startObstacleList, 80);
		ud2 = new UserDisplay(2, true, 4, 150, startObstacleList, 80);
		udList.add(ud1);
		udList.add(ud2);
	}
	
	private void initRepaint() {
		Timer repaintTimer = new Timer(50, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.repaint();
			}
		});
		//repaintTimer.start();
	}
	
	private void initTimer() {
		t = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myDisplayWriter();
				scoreLabel.setText(score = String.format("%05d", (int) (System.currentTimeMillis() - startTime) / 10));
				if (isRunningBy4) {
					for (Obstacle obs : liveObstacleList) {
						if (obs.getX() < 120 && obs.getX() + obs.getWidth() > 20) {
							if (obs.getY() + obs.getHeigth() >= 180) {
								JOptionPane.showInternalMessageDialog(getContentPane(), "Your Score is " + score);
								isAlive = false;
								// System.exit(0);
							}
						}
					}
				} else if (isJumping) {
					for (Obstacle obs : liveObstacleList) {
						if (obs.getX() < 70 && obs.getX() + obs.getWidth() > 20) {
							if (pikaY + 80 > obs.getY()) {
								jump.interrupt();
								JOptionPane.showInternalMessageDialog(getContentPane(), "Your Score is " + score);
								isAlive = false;
								// System.exit(0);
							}
						}
					}
				} else {
					for (Obstacle obs : liveObstacleList) {
						if (obs.getX() < 70 && obs.getX() + obs.getWidth() > 20) {
							if (pikaY + 80 > obs.getY()) {
								JOptionPane.showInternalMessageDialog(getContentPane(), "Your Score is " + score);
								isAlive = false;
								// System.exit(0);
							}
						}
					}
				}
			}
		});
	}

	private void myDisplayWriter() {
		try {
			UserDisplay myDisplay = new UserDisplay(myNumber, isAlive, imgNum, pikaY, liveObstacleList, lineX);
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
								pikaY = 150 - (int) ((Math.pow((jumpTime - 300), 2) / -600) + 150);
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

		obstacleList.add(new Obstacle(0, width, 150, groObsImg1.getWidth(this), groObsImg1.getHeight(this)));
		obstacleList.add(new Obstacle(1, width, 150, groObsImg2.getWidth(this), groObsImg2.getHeight(this)));
		
		skyObsImg1 = new ImageIcon("src/images/skyObstacle_뭐야이건.png").getImage();
		obsList.add(skyObsImg1);

		obstacleList.add(new Obstacle(2, width, 80, skyObsImg1.getWidth(this), skyObsImg1.getHeight(this)));

		moveObstacle();
		manageObstacle();
	}

	private void initComponent() {

		// pikachuReadyImg = new ImageIcon("src/images/pikachuReady.png").getImage();

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
		
		if (playerNumber == 1) {
			canvas = new JPanel() {
				@Override
				public void paint(Graphics g) {
					g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					g.drawLine(10, 220, lineX, 220);
					drawObstacle(g);
					g.drawImage(jumpImage, 20, pikaY, this);
					g.drawImage(runImage, 20, 150, this);
					g.drawImage(pikachuReadyImg, 20, 150, this);
				}

			};
		} else if (playerNumber <= 2) {
			setBounds(550, 100, 600, 300*playerNumber);
			canvas = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					synchronized (udList) {
						for(UserDisplay tmpud : udList) {
							int n = tmpud.pikachuNumber-1;
							g.drawLine(10, 220 + 300*n, tmpud.lineX, 220 + 300*n);
							for (Obstacle obs : tmpud.liveObstacleList) {
								g.drawImage(obsList.get(obs.getImgNum()), obs.getX(), obs.getY() + 300*n, this);
							}
							g.drawImage(pikaRunImgList.get(tmpud.imgNum), 20, tmpud.pikaY + 300*n, this);
						}
					}
				}

			};
		}

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
			}

		}.start();
	}

	private void drawObstacle(Graphics g) {
		for (Obstacle obs : liveObstacleList) {
			g.drawImage(obsList.get(obs.getImgNum()), obs.getX(), obs.getY(), this);
		}
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
									obs.getHeigth());
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

	public static void main(String[] args) {
		new Player_network();
	}

}
