package edu.utas.kit418.pengd.model;

import java.util.Random;

import edu.utas.kit418.pengd.ParallelMatrixMultiply;

public class Matrix {
	private static final String ERROR_MSG_0 = "Invalid matrix demision";
	public int rows, cols;
	public int[] value;
	private String name="";

	/* for Debugging */
	public Matrix(int[][] mat) throws Exception {
		if (mat != null && mat.length > 0) {
			rows = mat.length;
			cols = mat[0].length;
			value = new int[rows * cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					value[i * cols + j] = mat[i][j];
				}
			}
		} else {
			throw new Exception(ERROR_MSG_0);
		}
	}

	public Matrix(int rowNum, int colNum, boolean populate, String name) throws Exception {
		if (rowNum <= 0 || colNum <= 0) {
			throw new Exception(ERROR_MSG_0);
		}
		rows = rowNum;
		cols = colNum;
		value = new int[rows * cols];
		this.name = name;

		if (populate)
			randomPopulate(1, 10);
	}

	public void randomPopulate(int min, int max) {
		Random rand = new Random();
		for (int i = 0; i < value.length; i++) {
			value[i] = rand.nextInt((max - min) + 1) + min;
		}
	}

	public int[] row(int rowNum) {
		if (rowNum >= 0 && rowNum < rows) {
			int[] row = new int[cols];
			for (int i = 0; i < cols; i++) {
				row[i] = value[rowNum * cols + i];
			}
			return row;
		} else {
			return null;
		}
	}

	public int[] col(int colNum) {
		if (colNum < 0 || colNum >= cols)
			return null;
		int[] col = new int[rows];
		for (int i = 0; i < rows; i++) {
			col[i] = value[i * cols + colNum];
		}
		return col;
	}

	public void setValue(int x, int y, int v) {
		value[x * rows + y] = v;
	}

	public void printMatrix() {
		System.out.print(returnMatrix());
	}

	public String returnMatrix() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(":");
		sb.append(ParallelMatrixMultiply.newline);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				sb.append(value[i * cols + j] + " ");
			}
			sb.append(ParallelMatrixMultiply.newline);
		}
		return sb.toString();
	}
}
