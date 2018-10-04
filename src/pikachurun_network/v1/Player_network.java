package pikachurun_network.v1;

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

import pikachurun.v2.Obstacle;

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
	private Image pikaRunBy2_3Img;
	private Image pikaRunBy2_4Img;

	private Image groObsImg1;
	private Image groObsImg2;

	private Image skyObsImg1;

	private Image jumpImage;
	private Image runImage;
	private Image nothing = new ImageIcon("src/images/nothing.png").getImage();

	private List<Image> pikaRunBy4List = new ArrayList<>();
	private List<Image> pikaRunBy2List = new ArrayList<>();
	private List<Obstacle> obstacleList = new ArrayList<>();
	private List<Obstacle> liveObstacleList = new ArrayList<>();

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
	

	ObjectInputStream ois;
	ObjectOutputStream oos;

	private Player_network() {
		setTitle("Main Frame");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(550, 250, 600, 300);
		width = getWidth();
		initSocket();
		initReceiver();
		initComponent();
		initObstacle();
		initPikachuRun();
		initEvent();
		setVisible(true);
		initTimer();
	}

	private void initReceiver() {
		try {
			playerNumber = (Integer) ois.readObject();
			myNumber = (Integer) ois.readObject();
			ud = (UserDisplay) ois.readObject();
			while (true) {
				if(ud.isAlive == false) break;
				ud = (UserDisplay) ois.readObject();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

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

	private void initTimer() {
		t = new javax.swing.Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.repaint();
				myDisplayWriter();
				scoreLabel.setText(score = String.format("%05d", (int) (System.currentTimeMillis() - startTime) / 10));
				if (isRunningBy4) {
					for (Obstacle obs : liveObstacleList) {
						if (obs.getX() < 120 && obs.getX() + obs.getWidth() > 20) {
							if (obs.getY() + obs.getHeigth() >= 180) {
								JOptionPane.showInternalMessageDialog(getContentPane(), "You Died");
								isAlive = false;
								//System.exit(0);
							}
						}
					}
				} else if (isJumping) {
					for (Obstacle obs : liveObstacleList) {
						if (obs.getX() < 70 && obs.getX() + obs.getWidth() > 20) {
							if (pikaY + 80 > obs.getY()) {
								jump.interrupt();
								JOptionPane.showInternalMessageDialog(getContentPane(), "You Died");
								isAlive = false;
								//System.exit(0);
							}
						}
					}
				} else {
					for (Obstacle obs : liveObstacleList) {
						if (obs.getX() < 70 && obs.getX() + obs.getWidth() > 20) {
							if (pikaY + 80 > obs.getY()) {
								JOptionPane.showInternalMessageDialog(getContentPane(), "You Died");
								isAlive = false;
								//System.exit(0);
							}
						}
					}
				}
			}
		});
	}

	protected void myDisplayWriter() {
		try {
			Image image = jumpImage;
			if(!isJumping) {
				image = runImage;
			}
			UserDisplay myDisplay = new UserDisplay(myNumber, isAlive, image, pikaY, liveObstacleList);
			oos.writeObject(myDisplay);
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
				runImage = pikaRunBy4List.get(i++);
			}
		});
		runBy4.start();
		runBy4.stop();

		runBy2 = new Timer(70, new ActionListener() {
			int i = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				jumpImage = nothing;
				if (i == 4)
					i = 0;
				runImage = pikaRunBy2List.get(i++);
			}

		});

	}

	private void initObstacle() {
		groObsImg1 = new ImageIcon("src/images/groundObstacle_잠만보.png").getImage();
		groObsImg2 = new ImageIcon("src/images/groundObstacle_이브이.png").getImage();

		obstacleList.add(new Obstacle(groObsImg1, width, 150, groObsImg1.getWidth(this), groObsImg1.getHeight(this)));
		obstacleList.add(new Obstacle(groObsImg2, width, 150, groObsImg2.getWidth(this), groObsImg2.getHeight(this)));

		skyObsImg1 = new ImageIcon("src/images/skyObstacle_뭐야이건.png").getImage();

		obstacleList.add(new Obstacle(skyObsImg1, width, 80, skyObsImg1.getWidth(this), skyObsImg1.getHeight(this)));

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
		pikaRunBy2_3Img = new ImageIcon("src/images/pikachuRunBy2_1.png").getImage();
		pikaRunBy2_4Img = new ImageIcon("src/images/pikachuRunBy2_2.png").getImage();

		jumpImage = nothing;
		runImage = nothing;
		pikachuReadyImg = pikaRunBy2_1Img;

		pikaRunBy4List.add(pikaRun4Img);
		pikaRunBy4List.add(pikaRun1Img);
		pikaRunBy4List.add(pikaRun2Img);
		pikaRunBy4List.add(pikaRun3Img);

		pikaRunBy2List.add(pikaRunBy2_4Img);
		pikaRunBy2List.add(pikaRunBy2_1Img);
		pikaRunBy2List.add(pikaRunBy2_2Img);
		pikaRunBy2List.add(pikaRunBy2_3Img);

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
		} else if (playerNumber == 2) {
			setBounds(550, 100, 600, 600);
			canvas = new JPanel() {
				@Override
				public void paint(Graphics g) {
					g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					g.drawLine(10, 220, lineX, 220);
					drawObstacle(g);
					g.drawImage(jumpImage, 20, pikaY, this);
					g.drawImage(runImage, 20, 150, this);
					g.drawImage(pikachuReadyImg, 20, 150, this);

					g.drawLine(10, 220, ud.lineX, 220);
					for (Obstacle obs : ud.liveObstacleList) {
						g.drawImage(obs.getImage(), obs.getX(), obs.getY(), this);
					}
					g.drawImage(jumpImage, 20, ud.pikaY, this);
					g.drawImage(runImage, 20, 150, this);
					g.drawImage(pikachuReadyImg, 20, 150, this);
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
			g.drawImage(obs.getImage(), obs.getX(), obs.getY(), this);
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
							Obstacle obsClone = new Obstacle(obs.getImage(), obs.getX(), obs.getY(), obs.getWidth(),
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
				moveObstacle = new Timer(5, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						for (Obstacle obs : liveObstacleList) {
							obs.setX(obs.getX() - 5);
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
