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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.berber.kindle.annotator.model.State;
import de.berber.kindle.annotator.model.Task;

/**
 * A cell renderer for the Task structure.
 * 
 * @author Bernhard J. Berger
 */
public class TaskCellRenderer extends JLabel implements ListCellRenderer {
	/**
	 * Generated uid
	 */
	private static final long serialVersionUID = 2727365468240026536L;

	/**
	 * Each task state has its own icon, which will be cached here to avoid
	 * loading it multiple times.
	 */
	private final Map<State, ImageIcon> stateToIcon = new HashMap<State, ImageIcon>();

	/**
	 * Default constructor.
	 */
	public TaskCellRenderer() {
		// load and cache state icons
		stateToIcon.put(State.WAITING, new ImageIcon(ListModelAdapter.class.getClassLoader().getResource("de/berber/kindle/annotator/gui/waiting.png")));
		stateToIcon.put(State.RUNNING, new ImageIcon(ListModelAdapter.class.getClassLoader().getResource("de/berber/kindle/annotator/gui/running.png")));
		stateToIcon.put(State.FINISHED, new ImageIcon(ListModelAdapter.class.getClassLoader().getResource("de/berber/kindle/annotator/gui/finished.png")));
		stateToIcon.put(State.ERROR, new ImageIcon(ListModelAdapter.class.getClassLoader().getResource("de/berber/kindle/annotator/gui/error.png")));
		stateToIcon.put(State.ABORTED, new ImageIcon());

		// set up look and feel
		setFont(new Font("Arial Black", Font.PLAIN, 20));
		setHorizontalTextPosition(RIGHT);
		setVerticalAlignment(CENTER);
		setForeground(new Color(0.2f, 0.2f, 0.2f));
	}
	
	/**
	 * Render component
	 */
	public Component getListCellRendererComponent(final @Nonnull JList view,
				                                  final @Nonnull Object obj,
				                                  int arg2, boolean arg3, boolean arg4) {
		final Task task = (Task) obj;
		
		this.setIcon(stateToIcon.get(task.getState()));
		this.setText(task.getInputFile().getName());

		return this;
	}
}
