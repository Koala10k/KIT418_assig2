package edu.utas.kit418.pengd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import mpi.MPI;
import mpi.Request;
import edu.utas.kit418.pengd.manager.TaskManager;
import edu.utas.kit418.pengd.model.Matrix;
import edu.utas.kit418.pengd.utilities.ConfigParser;

public class ParallelMatrixMultiply {
	private static final int master = 0;
	private static final int multiply = 0, result = 1, tag_bufSize = 2;
	protected static final String PARTTEN_NUMBERS = "^[1-9]\\d*$";

	public static String newline = System.getProperty("line.separator");
	public static String slash = File.separator;
	private static Matrix leftMatrix;
	private static Matrix rightMatrix;
	private static Matrix resultMatrix;

	private static GUI gui;

	private static boolean isGUI;
	private static int param1;
	private static int param2;
	private static int param3;

	protected enum State {
		STATE_INIT, STATE_MATRIX_INIT_READY, STATE_READY_TO_START
	}

	protected static State state = State.STATE_INIT;
	protected static int size;

	private static TaskDoneListener taskDoneLst;

	protected interface TaskDoneListener {
		public void done(long l);
	}

	public static void main(String[] args){
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		size = MPI.COMM_WORLD.Size();

		if (rank == master) {
			File confFile = new File("config.xml");
			if (!confFile.exists()) {
				try {
					Files.copy(new File("res" + File.separatorChar + "ConfigTemplete.xml").toPath(), confFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("generating config file...");
			}
			if (ConfigParser.parse(confFile) == ConfigParser.State.SUCCESS) {
				isGUI = ConfigParser.getMode().equalsIgnoreCase("GUI");
				param1 = ConfigParser.getRow1();
				param2 = ConfigParser.getCol1row2();
				param3 = ConfigParser.getCol2();
			} else {
				printErr(ConfigParser.getErrMsg());
			}

			if (isGUI) {
				printLog("starting in GUI mode...");
				gui = new GUI();
				taskDoneLst = gui;
				gui.setInitParams(param1, param2, param3);
				gui.setVisible(true);
			} else {
				printLog("starting in CMD mode...");
				generateMatrix(param1, param2, param3);
				state = State.STATE_READY_TO_START;
			}
			while (true) {
				while (state != State.STATE_READY_TO_START) {
					System.out.print("");
				}
				long startTime = System.currentTimeMillis();
				try {
					resultMatrix = new Matrix(leftMatrix.rows, rightMatrix.cols, false, "Result Matrix");
				} catch (Exception e) {
				}
				boolean[] nodeState = new boolean[size - 1];
				TaskManager taskmgr = new TaskManager(leftMatrix.rows, rightMatrix.cols, size - 1);
				int[] pos;
				boolean running1 = true, running2 = true;
				while (running1 || running2) {
					if (running1) {
						for (int i = 1; i < size; i++) {
							if (!nodeState[i - 1]) {
								pos = taskmgr.next();
								if (pos == null) {
									running1 = false;
									break;
								}
								taskmgr.bind(i - 1, pos);
								nodeState[i - 1] = true;
								MPI.COMM_WORLD.Isend(new int[] { leftMatrix.cols }, 0, 1, MPI.INT, i, tag_bufSize);
								int[] data = new int[leftMatrix.cols * 2];
								for (int j = 0; j < leftMatrix.cols; j++) {
									data[j] = leftMatrix.row(pos[0])[j];
									data[j + leftMatrix.cols] = rightMatrix.col(pos[1])[j];
								}
								MPI.COMM_WORLD.Isend(data, 0, leftMatrix.cols * 2, MPI.INT, i, multiply);
							}
						}
					}
					if (running2) {
						for (int i = 1; i < size; i++) {
							if (nodeState[i - 1]) {
								int[] taskPos = taskmgr.taskPos(i - 1);
								int[] ret = new int[1];
								Request req = MPI.COMM_WORLD.Irecv(ret, 0, 1, MPI.INT, i, result);
								req.Wait(); // <--------------------------------------------------------don't
											// want to wait here
								if (req == null || req.Test() == null) {
									System.out.println("recv nothing");
									continue;
								} else {
									resultMatrix.setValue(taskPos[0], taskPos[1], ret[0]);
									nodeState[i - 1] = false;
									if (!running1) {
										boolean busy = false;
										for (int j = 0; j < nodeState.length; j++)
											busy |= nodeState[j];
										if (!busy)
											running2 = false;
									}
								}
							}
						}
					}
				}
				printLog(resultMatrix.returnMatrix());
				if (isGUI) {
					state = State.STATE_INIT;
				} else {
					for (int i = 1; i < size; i++) {
						MPI.COMM_WORLD.Isend(new int[] { 0 }, 0, 1, MPI.INT, i, tag_bufSize);
					}
					printLog("Time Elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
					break;
				}
				if (taskDoneLst != null) {
					taskDoneLst.done(System.currentTimeMillis() - startTime);
				}
			}
		} else {
			while (true) {
				int[] bufSize = new int[1];
				MPI.COMM_WORLD.Recv(bufSize, 0, 1, MPI.INT, master, tag_bufSize);
				if (bufSize[0] == 0)
					break;
				int[] recvbuf = new int[bufSize[0] * 2];
				MPI.COMM_WORLD.Recv(recvbuf, 0, bufSize[0] * 2, MPI.INT, master, multiply);
				int rst = 0;
				for (int i = 0; i < recvbuf.length / 2; i++) {
					rst += recvbuf[i] * recvbuf[i + bufSize[0]];
				}
				/*
				 * if (rank == 2) try { Thread.sleep(2000); //
				 * <----------------------------------------------sleep // 2s to
				 * simulate network latency } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
				MPI.COMM_WORLD.Send(new int[] { rst }, 0, 1, MPI.INT, master, result);
			}
		}
		MPI.Finalize();
	}

	private static void printErr(String msg) {
		System.err.println("Error: "+msg);
		System.exit(1);
	}

	public static void generateMatrix(int row1, int col1row2, int col2) {
		printLog("generating matrix...");
		try {
			leftMatrix = new Matrix(row1, col1row2, true, "Left Matrix");
			rightMatrix = new Matrix(col1row2, col2, true, "Right Matrix");
		} catch (Exception e) {
			printErr(e.getMessage());
		}

		printLog(leftMatrix.returnMatrix());
		printLog(rightMatrix.returnMatrix());
	}

	private static void printLog(String msg) {
		if (gui != null) {
			gui.log(msg + newline);
		} else {
			System.out.println(msg);
		}
	}

}
