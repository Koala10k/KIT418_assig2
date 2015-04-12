package edu.utas.kit418.pengd.manager;

public class TaskManager {

	private int rows, cols;
	private int rowPos = 0, colPos = -1;
	private int[][] mapper;
	private int nodeSize;

	public TaskManager(int rows, int cols, int size) {
		this.rows = rows;
		this.cols = cols;
		mapper = new int[size][2];
		nodeSize = size;
	}

	public int[] next() {
		colPos++;
		if (colPos >= cols) {
			colPos = 0;
			rowPos++;
		} 
		if (rowPos >= rows)
			return null;
		return new int[] { rowPos, colPos };
	}

	public void bind(int i, int[] pos) {
		mapper[i][0] = pos[0];
		mapper[i][1] = pos[1];
	}

	public int[] taskPos(int i) {
		if (i < 0 || i > nodeSize)
			return null;
		else {
			return mapper[i];
		}
	}

}
