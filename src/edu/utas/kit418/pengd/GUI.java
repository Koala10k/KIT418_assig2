package edu.utas.kit418.pengd;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import edu.utas.kit418.pengd.ParallelMatrixMultiply.TaskDoneListener;

public class GUI extends JFrame implements TaskDoneListener {
	private static final long serialVersionUID = 1L;
	private JTextArea logPane;
	private JScrollPane jScrollPane;
	private JPanel controlPane;
	private JButton jBtnClearLog;
	private JButton jBtnGenerate;
	private JButton jBtnCompute;
	private JSplitPane jSplit;
	private JTextField jTxtMxt1Rows;
	private JLabel jLblMxt1Rows;
	private JLabel jLblMxt1Cols;
	private JLabel jLblMxt2Cols;
	private JTextField jTxtMxt1Cols;
	private JTextField jTxtMxt2Cols;
	private JLabel jLblWorkerNodes;
	private JLabel jLblTimeElapsed;

	public GUI() {
		setTitle("Cluster Computing GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage("res"+File.separatorChar+"icon.png"));
		createComponents();
		performLayout();
		adjust();
	}

	private void createComponents() {

		logPane = new JTextArea();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		logPane.setLineWrap(false);
		logPane.setWrapStyleWord(true);
		logPane.setEditable(false);

		jScrollPane = new JScrollPane(logPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
		
		controlPane = new JPanel();
		controlPane.setBorder(BorderFactory.createTitledBorder("Options"));

		jBtnClearLog = new JButton("Clear Log");
		jBtnGenerate = new JButton("Generate");
		jBtnCompute = new JButton("Compute");
		jLblMxt1Rows = new JLabel("Amount of rows of Matrix 1 [1-500]:");
		jLblMxt1Cols = new JLabel("Amount of cols of Matrix 1 [1-500]:");
		jLblMxt2Cols = new JLabel("Amount of cols of Matrix 2 [1-500]:");
		jTxtMxt1Rows = new JTextField();
		jTxtMxt1Cols = new JTextField();
		jTxtMxt2Cols = new JTextField();
		jLblWorkerNodes = new JLabel("Amount of Workers: " + (ParallelMatrixMultiply.size - 1));
		jLblTimeElapsed = new JLabel("Time Elapsed: 0");

		jBtnCompute.setEnabled(false);
		jBtnClearLog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logPane.setText("");
			}
		});

		jBtnGenerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTxtMxt1Rows.getText().matches(ParallelMatrixMultiply.PARTTEN_NUMBERS) && jTxtMxt1Cols.getText().matches(ParallelMatrixMultiply.PARTTEN_NUMBERS)
						&& jTxtMxt2Cols.getText().matches(ParallelMatrixMultiply.PARTTEN_NUMBERS) && Integer.parseInt(jTxtMxt1Rows.getText()) > 0 && Integer.parseInt(jTxtMxt1Rows.getText()) <= 1000
						&& Integer.parseInt(jTxtMxt1Cols.getText()) > 0 && Integer.parseInt(jTxtMxt1Cols.getText()) <= 1000 && Integer.parseInt(jTxtMxt2Cols.getText()) > 0
						&& Integer.parseInt(jTxtMxt2Cols.getText()) <= 1000) {
					ParallelMatrixMultiply.generateMatrix(Integer.parseInt(jTxtMxt1Rows.getText()), Integer.parseInt(jTxtMxt1Cols.getText()), Integer.parseInt(jTxtMxt2Cols.getText()));
					ParallelMatrixMultiply.state = ParallelMatrixMultiply.State.STATE_MATRIX_INIT_READY;
					jBtnCompute.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(GUI.this, "Error: <rows> or <cols> must be a positive integer ranging from 1 to 100", "Invalid parameter", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jBtnCompute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ParallelMatrixMultiply.state == ParallelMatrixMultiply.State.STATE_MATRIX_INIT_READY)
					ParallelMatrixMultiply.state = ParallelMatrixMultiply.State.STATE_READY_TO_START;
			}
		});

		jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jScrollPane, controlPane);
		jSplit.setOneTouchExpandable(true);

	}

	private void performLayout() {
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) screenSize.getWidth() / 4, (int) screenSize.getHeight() / 4);
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		SequentialGroup h1 = layout.createSequentialGroup();
		h1.addComponent(jSplit, GroupLayout.DEFAULT_SIZE, (int) screenSize.getWidth() / 2, Short.MAX_VALUE);
		layout.setHorizontalGroup(h1);

		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		vGroup.addComponent(jSplit, GroupLayout.DEFAULT_SIZE, (int) screenSize.getHeight() / 2, Short.MAX_VALUE);
		layout.setVerticalGroup(vGroup);

		GroupLayout optLayout = new GroupLayout(controlPane);
		controlPane.setLayout(optLayout);
		optLayout.setAutoCreateContainerGaps(true);
		optLayout.setAutoCreateGaps(true);
		optLayout.setHorizontalGroup(optLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(jBtnClearLog, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(jBtnGenerate, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(jBtnCompute, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(
						optLayout.createSequentialGroup().addComponent(jLblMxt1Rows, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(jTxtMxt1Rows, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(
						optLayout.createSequentialGroup().addComponent(jLblMxt1Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(jTxtMxt1Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(
						optLayout.createSequentialGroup().addComponent(jLblMxt2Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(jTxtMxt2Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)).addComponent(jLblWorkerNodes).addComponent(jLblTimeElapsed));
		optLayout.setVerticalGroup(optLayout
				.createSequentialGroup()
				.addComponent(jBtnClearLog)
				.addComponent(jBtnGenerate)
				.addComponent(jBtnCompute)
				.addGroup(
						optLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLblMxt1Rows, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(jTxtMxt1Rows, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(
						optLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLblMxt1Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(jTxtMxt1Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(
						optLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLblMxt2Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(jTxtMxt2Cols, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)).addComponent(jLblWorkerNodes).addComponent(jLblTimeElapsed));
		pack();
	}

	private void adjust() {
		// setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		jSplit.setDividerLocation(0.7);
	}

	public void log(String info) {
		logPane.append(info);
	}

	@Override
	public void done(long timeElpased) {
		jBtnCompute.setEnabled(false);
		jLblTimeElapsed.setText("Time Elapsed: " + timeElpased + "ms");
	}

	public void setInitParams(int param1, int param2, int param3) {
		jTxtMxt1Rows.setText(String.valueOf(param1));
		jTxtMxt1Cols.setText(String.valueOf(param2));
		jTxtMxt2Cols.setText(String.valueOf(param3));
	}

}
