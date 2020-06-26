package jlife;

import javax.swing.*;

public class Main {
	public static void main(String[] argv) throws InterruptedException {

		JFrame frame = new JFrame("jlife");
		GamePanel gamePanel = new GamePanel();
		frame.add(gamePanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(false);
		frame.setIgnoreRepaint(true);
		frame.pack();
		frame.setResizable(true);
		frame.setVisible(true);

		while (gamePanel.animator == null) {
			Thread.sleep(10);
		};
		gamePanel.animator.join();
	}
}
