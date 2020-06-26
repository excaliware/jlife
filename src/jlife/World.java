package jlife;

import java.awt.*;

public class World {
	private static final int COLUMNS = 125;
	private static final int ROWS = 95;
	private static final int SCALE = 5;

	private boolean[][] world;
	private boolean[][] changes;

	public World() {
		world = new boolean[ROWS][COLUMNS];
		changes = new boolean[ROWS][COLUMNS];
		populate();
	}

	private void populate() {
		for (int i = ROWS / 3; i < ROWS / 1.5; i++) {
			for (int j = COLUMNS / 3; j < COLUMNS / 1.5; j++) {
				if ((int) (Math.random() * 10) == 0) {
					world[i][j] = true;
				}
			}
		}
	}

	public void live() {
		for (int i = 1; i < ROWS-1; i++) {
			for (int j = 1; j < COLUMNS-1; j++) {

				int numNeighbours = 0;
				for (int m = -1; m < 2; m++) {
					for (int n = -1; n < 2; n++) {
						if (world[i+m][j+n]) {
							numNeighbours++;
						}
					}
				}
				if (world[i][j]) {
					numNeighbours--;
				}

				if ((!world[i][j] && numNeighbours == 3) ||
						(world[i][j] && numNeighbours < 2 || numNeighbours > 3)) {
					changes[i][j] = true;
				}
			}
		}

		for (int i = 1; i < ROWS-1; i++) {
			for (int j = 1; j < COLUMNS-1; j++) {
				if (changes[i][j]) {
					world[i][j] = !world[i][j];
					changes[i][j] = false;
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder(ROWS * ((COLUMNS * 3)+1));

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				strBuilder.append(world[i][j] ? " # " : "   ");
			}
			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}

	public void draw(Graphics dbg) {
		dbg.setColor(Color.GREEN);

		for (int i = 1; i < ROWS-1; i++) {
			for (int j = 1; j < COLUMNS-1; j++) {
				if (world[i][j]) {
					dbg.fillOval(j * SCALE, i * SCALE, 5, 5);
				}
			}
		}
	}
}
