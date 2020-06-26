package jlife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {
	static final int SCREEN_WIDTH = 640;
	static final int SCREEN_HEIGHT = 480;
	private static final int FPS = 5;
	private static final long PERIOD = 1000 / FPS;

	private boolean running;
	private boolean gameOver;
	private boolean paused;
	// variables for double-buffering rendering
	private Graphics dbg;
	private Image dbImage;

	Thread animator;

	private World world;

	public GamePanel() {
		setBackground(Color.RED);
		setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

		setFocusable(true);
		requestFocus();
		readyForTermination();


		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				//testPress(e.getX(), e.getY());
			}
		});

		world = new World();
	}  // end of GamePanel()

	/**
	 * Repeatedly update, render, sleep.
	 */
	public void run() {
		try {
			if (paused) {
				synchronized (this) {
					while (paused && running)
						wait();
				}
			}
		}
		catch (InterruptedException e) {
			System.err.printf("wait(): %s", e.getMessage());
		}

		long beforeTime, afterTime, timeDiff, sleepTime;

		running = true;

		while (running) {
			beforeTime = System.currentTimeMillis();

			gameUpdate();
			gameRender();
			paintScreen();    // draw buffer to screen

			afterTime = System.currentTimeMillis();
			timeDiff = afterTime - beforeTime;
			sleepTime = (PERIOD - timeDiff);

			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					System.err.printf("sleep: %s", e.getMessage());
				}
			}
		}
		System.exit(0);
	} // end of run()

	/**
	 * Draw the current frame to an image buffer.
	 */
	private void gameRender() {
		// create the buffer
		if (dbImage == null) {
			dbImage = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
			if (dbImage == null) {
				System.err.println("dbImage is null");
				return;
			} else
				dbg = dbImage.getGraphics();
		}
		// clear the screen
		dbg.setColor(Color.BLACK);
		dbg.fillRect(1, 1, SCREEN_WIDTH - 2, SCREEN_HEIGHT - 2);


//		System.out.printf("%s", world);
		world.draw(dbg);

		if (gameOver)
			gameOverMessage(dbg);
	}  // end of gameRender()

	/**
	 * Wait for the JPanel to be added to the JFrame before starting.
	 */
	public void addNotify() {
		super.addNotify();   // creates the peer
		startGame();         // start the thread
	}

	/**
	 * Initialise and start the thread.
	 */
	private void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame()

	/**
	 * Called by the user to stop execution.
	 */
	public void stopGame() {
		running = false;
	}

	private void gameUpdate() {
		if (!paused && !gameOver) {
			world.live();
		}
	}

	/**
	 * Draw the game-over message.
	 */
	private void gameOverMessage(Graphics g) {
		g.drawString("The End", 10, 10);
	}  // end of gameOverMessage()

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dbImage != null)
			g.drawImage(dbImage, 0, 0, null);
	}

	/**
	 * Render the buffer image to the screen.
	 */
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if (g != null && dbImage != null) {
				g.drawImage(dbImage, 0, 0, null);
				Toolkit.getDefaultToolkit().sync();
				g.dispose();
			}
		} catch (Exception e) {
			System.err.println("Graphics context error: " + e);
		}
	} // end of paintScreen()

	private void readyForTermination() {
		addKeyListener(new KeyAdapter() {
			// listen for esc, q, ctrl-c
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) ||
						(keyCode == KeyEvent.VK_Q) ||
						((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					running = false;
				} else if (keyCode == KeyEvent.VK_SPACE) {
					if (!paused) {
						pauseGame();
					} else {
						resumeGame();
					}
				}
			}
		});
	}  // end of readyForTermination()

	public void pauseGame() {
		paused = true;
	}

	public void resumeGame() {
		paused = false;
	}
}
