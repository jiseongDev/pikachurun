package pikachurun.v1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Player2 extends JFrame {

	private JPanel pikaRunPanel;
	private JPanel panel;

	private ImageIcon pikaUpImgIcon;
	private JLabel pikaUp;

	private ImageIcon pikaRun1ImgIcon;
	private ImageIcon pikaRun2ImgIcon;
	private ImageIcon pikaRun3ImgIcon;
	private ImageIcon pikaRun4ImgIcon;
	
	private Image jumpImage;

	private JLabel pikaRun1;
	private JLabel pikaRun2;
	private JLabel pikaRun3;
	private JLabel pikaRun4;

	private CardLayout cardLayout;

	private List<JLabel> pikaRunList = new ArrayList<>();

	private Thread run;
	private Thread jump;

	private boolean isJumping;

	private Player2() {
		setTitle("Main Frame");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(550, 250, 600, 300);
		cardLayout = new CardLayout();
		initComponent();
		initPikachuMove();
		initEvent();
		setVisible(true);
	}

	private void initEvent() {
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (isJumping)
					return;
				run.interrupt();
				jump.start();
			}

		});
	}

	private void initPikachuMove() {
		run = new Thread() {

			@Override
			public void run() {
				while (true) {
					cardLayout.next(pikaRunPanel);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}

		};
		run.start();

		jump = new Thread() {
			
		};

	}

	private void initComponent() {
		pikaRunPanel = new JPanel(cardLayout);
		pikaUpImgIcon = new ImageIcon("src/images/pikachuUp.png");
		pikaUp = new JLabel();
		pikaUp.setIcon(pikaUpImgIcon);
		// pikaRunPanel.add(pikaUp);
		// add(pikaRunPanel);
		// add(pikaUp, BorderLayout.SOUTH);

		pikaRun1ImgIcon = new ImageIcon("src/images/pikachuRun1.png");
		pikaRun2ImgIcon = new ImageIcon("src/images/pikachuRun2.png");
		pikaRun3ImgIcon = new ImageIcon("src/images/pikachuRun3.png");
		pikaRun4ImgIcon = new ImageIcon("src/images/pikachuRun4.png");
		
		jumpImage = pikaRun1ImgIcon.getImage();

		pikaRun1 = new JLabel();
		pikaRun2 = new JLabel();
		pikaRun3 = new JLabel();
		pikaRun4 = new JLabel();

		pikaRun1.setIcon(pikaRun1ImgIcon);
		pikaRun2.setIcon(pikaRun2ImgIcon);
		pikaRun3.setIcon(pikaRun3ImgIcon);
		pikaRun4.setIcon(pikaRun4ImgIcon);


		pikaRunList.add(pikaRun4);
		pikaRunList.add(pikaRun1);
		pikaRunList.add(pikaRun2);
		pikaRunList.add(pikaRun3);

		pikaRunPanel.add(pikaRun4);
		pikaRunPanel.add(pikaRun1);
		pikaRunPanel.add(pikaRun2);
		pikaRunPanel.add(pikaRun3);


		panel = new JPanel() {
			
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(jumpImage, 300, 100, this);
			}
			
		};
		
		add(panel);
		add(pikaRunPanel, BorderLayout.SOUTH);

	}

	public static void main(String[] args) {
		new Player2();
		JPanel jpikaRunPanel = new JPanel() {

		};
	}

}
