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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import de.berber.kindle.annotator.model.State;
import de.berber.kindle.annotator.model.Task;
import de.berber.kindle.annotator.model.TaskListener;
import de.berber.kindle.annotator.model.WorkingList;
import de.berber.kindle.annotator.model.WorkingListListener;

/**
 * A simple adapter mediating between out internal working list and the JList
 *   ListModel.
 * 
 * @author Bernhard J. Berger
 */
public class ListModelAdapter extends AbstractListModel implements WorkingListListener, TaskListener {
	/**
	 * Gernerated UID
	 */
	private static final long serialVersionUID = -8842408435578577976L;
	
	/**
	 * The model we are wrapping.
	 */
	private final WorkingList model;
	
	/**
	 * Creates a new adapter for {@code workingList} and registers for change
	 * notifications.
	 */
	public ListModelAdapter(final @Nonnull WorkingList workingList) {
		this.model = workingList;
		
		this.model.addListener(this);
	}

	/**
	 * If a task was added we have to inform the ui part. And register for
	 *   task notifications.
	 */
	public void taskAdded(final @Nonnull Task task) {
		task.addListener(this);
		
		final int size = getSize();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireIntervalAdded(this, size, size);
			}
		});
	}

	/**
	 * Inform the ui part about the model change.
	 */
	public void modelCleared() {
		fireIntervalRemoved(this, 0, getSize());
	}

	public void completedWorklist() {
		// nothing to do
	}

	/**
	 * If a task changed its state we have to trigger an ui refresh.
	 */
	public void stateChange(final @Nonnull Task task, final @Nonnull State oldState, final @Nonnull State newState) {
		final int index = model.indexOf(task);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireContentsChanged(this, index, index);
			}
		});
	}

	/**
	 * Returns task at position {@code column}.
	 */
	public Object getElementAt(@Nonnegative int column) {
		return model.getTask(column);
	}

	/**
	 * Returns the number of actual tasks.
	 */
	public @Nonnegative int getSize() {
		return model.getNumberOfTasks();
	}
}
