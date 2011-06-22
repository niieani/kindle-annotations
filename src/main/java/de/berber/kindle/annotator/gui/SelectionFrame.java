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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BoxLayout;
import javax.swing.Box;

/**
 * TODO Document me.
 * 
 * @author Bernhard J. Berger
 */
public class SelectionFrame extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5301032389715744561L;
	
	private JTextField textField;
	private JLabel lblTitle;
	private Component horizontalStrut;
	private Component horizontalStrut_1;
	
	//int height, width;
	
	public SelectionFrame(final String labelText) {
		setBorder(new LineBorder(Color.BLUE));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		horizontalStrut = Box.createHorizontalStrut(20);
		add(horizontalStrut);
		
		lblTitle = new JLabel(labelText);
		add(lblTitle);
		lblTitle.setPreferredSize(new Dimension(100, 0));
		
		textField = new JTextField();
		add(textField);
		textField.setColumns(1);
		textField.setBorder(new LineBorder(Color.GREEN));
		
		horizontalStrut_1 = Box.createHorizontalStrut(20);
		add(horizontalStrut_1);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final Dimension size = getSize();
		
		//System.out.println(g.getClip());
		g.setColor(Color.BLACK);
		g.drawArc(15, 0, size.height, size.height, 90, 180);
		g.drawArc(size.width - 15, 0, size.height, size.height, 90, -180);
	}
}
