/*
 * Copyright 2011, Bernhard J. Berger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.berber.kindle.annotator.gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.BoxLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import de.berber.kindle.annotator.controller.WorkCollector;
import de.berber.kindle.annotator.model.Task;
import de.berber.kindle.annotator.model.WorkingList;
import de.berber.kindle.annotator.model.WorkingListListener;

/**
 * The applications main window.
 * 
 * @author Bernhard J. Berger
 * 
 * TODO Document me
 */
public class MainWindow implements ActionListener, WorkingListListener {

	private JFrame frmPdfannotatorGui;
	private JTextField inputTextField;
	private JTextField outputTextField;
	private JList fileList;
	private WorkingList workingList;
	private JButton selectInputBtn;
	private JButton goBtn;
	private JButton selectOutputBtn;
	private JButton clearBtn;

	/**
	 * Create the application.
	 * @param cc 
	 */
	public MainWindow(final WorkingList workingList) {
		this.workingList = workingList;
		
		workingList.addListener(this);

		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		final int strutSize = 20;
		
		frmPdfannotatorGui = new JFrame();
		frmPdfannotatorGui.setTitle("PDFAnnotator GUI");
		frmPdfannotatorGui.setSize(800, 600);
		frmPdfannotatorGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmPdfannotatorGui.setJMenuBar(menuBar);
		
		Box verticalBox = Box.createVerticalBox();
		frmPdfannotatorGui.getContentPane().add(verticalBox, BorderLayout.CENTER);
		
		final JPanel inputPanel = new JPanel();
		verticalBox.add(inputPanel);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.add(Box.createHorizontalStrut(strutSize));
		
		final Dimension lblWidth = new Dimension(80,0);
		
		final JLabel lblInput = new JLabel("Input");
		lblInput.setToolTipText("Input file or directory");
		inputPanel.add(lblInput);
		lblInput.setPreferredSize(lblWidth);
		inputTextField = createTextField(inputPanel);
		JPanel panel = new JPanel();
		inputPanel.add(panel);
		
		selectInputBtn = addButton("Select", Action.SELECT_INPUT, panel);
		goBtn = addButton("Go", Action.GO, panel);
		//addButton("Reload", Action.RELOAD, panel);
		
		inputPanel.add(Box.createHorizontalStrut(strutSize));
		
		verticalBox.add(Box.createVerticalStrut(strutSize));
		
		final JPanel outputPanel = new JPanel();
		verticalBox.add(outputPanel);
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
		
		outputPanel.add(Box.createHorizontalStrut(strutSize));
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setToolTipText("Output file or directory. May be empty.");
		outputPanel.add(lblOutput);
		lblOutput.setPreferredSize(lblWidth);
		
		outputTextField = createTextField(outputPanel);
		
		panel = new JPanel();
		outputPanel.add(panel);
		
		selectOutputBtn = addButton("Select", Action.SELECT_OUTPUT, panel);
		clearBtn = addButton("Clear", Action.CLEAR, panel);
		
		outputPanel.add(Box.createHorizontalStrut(strutSize));
		
		verticalBox.add(Box.createVerticalStrut(strutSize));
		
		fileList = new JList();
		fileList.setCellRenderer(new TaskCellRenderer());
		fileList.setFont(new Font("Arial Black", Font.PLAIN, 20));
		fileList.setModel(new ListModelAdapter(workingList));
		fileList.setFocusable(false);

		
		JScrollPane scrollPane = new JScrollPane(fileList);
		verticalBox.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(500, frmPdfannotatorGui.getSize().height - inputPanel.getPreferredSize().height - outputPanel.getPreferredSize().height));
		
		verticalBox.add(Box.createVerticalGlue());
	}

	private JTextField createTextField(final JPanel parent) {
		final JTextField textField = new JTextField();
		parent.add(textField);
		textField.setColumns(10);
		textField.setEditable(false);
	
		return textField;
	}

	private JButton addButton(final String text, final Action action, final JPanel parent) {
		final JButton button = new JButton(text);
		button.setActionCommand("btn-" + action.toString());
		button.addActionListener(this);
		parent.add(button);
		
		return button;
	}

	public void run() {
		frmPdfannotatorGui.setVisible(true);
	}
	
	static enum Action {
		CLEAR,
		GO,
		RELOAD,
		SELECT_INPUT,
		SELECT_OUTPUT
	}

	public void actionPerformed(final ActionEvent actionEvent) {
		if(actionEvent.getActionCommand().startsWith("btn-")) {
			final Action command = Action.valueOf(actionEvent.getActionCommand().substring(4));
			
			switch(command) {
			case CLEAR:
				outputTextField.setText(null);
				break;
				
			case GO:
				final File inputFile = new File(inputTextField.getText()); // TODO Check 
				final File outputFile = new File(outputTextField.getText()); // TODO Check 
				final WorkCollector collector = new WorkCollector(inputFile, outputFile, workingList);
				
				setInteractionState(false);
				
				new Thread(collector).run();
				break;
				
			case SELECT_INPUT:
			{
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(fileChooser.showOpenDialog(frmPdfannotatorGui) == JFileChooser.APPROVE_OPTION) {
					inputTextField.setText(fileChooser.getSelectedFile().toString());
				}
				break;
			}
			
			case SELECT_OUTPUT:
			{
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(fileChooser.showOpenDialog(frmPdfannotatorGui) == JFileChooser.APPROVE_OPTION) {
					outputTextField.setText(fileChooser.getSelectedFile().toString());
				}
				break;
			}
				
			default:
				assert false : "Unknown case " + command;
				break;
			}
		} else {
			System.out.println("Command triggered " + actionEvent.getActionCommand());
		}
	}

	/**
	 * Sets the interaction state to {@code true} or {@code false}. And allows
	 *   user input or not.
	 */
	private void setInteractionState(final boolean state) {
		selectInputBtn.setEnabled(state);
		goBtn.setEnabled(state);
		selectOutputBtn.setEnabled(state);
		clearBtn.setEnabled(state);
	}

	public void taskAdded(Task task) {
		// nothing to do 
	}

	public void modelCleared() {
		// nothing to do 
	}

	/**
	 * After receiving the work completed notification we can reset the
	 * 	interaction state.
	 */
	public void completedWorklist() {
		setInteractionState(true);
	}
}
