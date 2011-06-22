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
package de.berber.kindle.annotator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

/**
 * The list of current conversion tasks. The WorkingList is the central
 * synchronization point of all concurrent threads.
 * 
 * @author Bernhard J. Berger
 */
public class WorkingList implements TaskListener {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(WorkingList.class);	
	
	/**
	 * List of current tasks. Assume a work list size of 100 entries to
	 * avoid resizing.
	 */
	private List<Task> tasks = new ArrayList<Task>(100);
	
	/**
	 * Number of unfinished tasks, we still monitor.
	 */
	private int numberOfUnfinishedTasks = 0;
	
	/**
	 * Number of parallel collectors
	 */
	private int numberOfCollectors = 0;

	/**
	 * Set of listeners interested in changes of the data model.
	 */
	private Set<WorkingListListener> listeners = new HashSet<WorkingListListener>();

	/**
	 * Adds a new listener to the set of listeners.
	 * 
	 * @param listener The listener instance to add
	 */
	public final void addListener(final WorkingListListener listener) {
		LOG.info("Adding listener: " + listener);
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Removes a listener from the set of listeners.
	 * 
	 * @param listener The listener to remove.
	 */
	public final void removeListener(final WorkingListListener listener) {
		LOG.info("Removing listener: " + listener);
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Adds a new task to the working list.
	 * 
	 * @param inputFile Source file.
	 * @param outputFile Target file.
	 */
	public final void addTask(final File inputFile, final File outputFile) {
		final Task task = new Task(inputFile, outputFile);
		LOG.info("Adding task: " + task);
		
		synchronized (tasks) {
			tasks.add(task);
			numberOfUnfinishedTasks += 1;

			// listen for state changes to keep numberOfUnfinishedTasks up to
			// date.
			task.addListener(this);
		}
		
		synchronized(listeners) {
			for(WorkingListListener listener : listeners) {
				listener.taskAdded(task);
			}
		}
	}

	/**
	 * Clear the working list (remove all tasks).
	 */
	public final void clear() {
		synchronized(tasks) {
			tasks.clear();
			numberOfUnfinishedTasks = 0;
		}
		
		synchronized(listeners) {
			for(WorkingListListener listener : listeners) {
				listener.modelCleared();
			}
		}
	}

	/**
	 * A collector notifies the working list about its existence.
	 */
	public synchronized void collectorStarts() {
		numberOfCollectors  += 1;
	}
	
	/**
	 * A collector has finished its operations.
	 */
	public final void collectorFinished() {
		synchronized(this) {
			numberOfCollectors -= 1;
		}
		checkFinished();
	}

	/**
	 * Check if all collectors are finished and the tasks has been completed.
	 */
	private void checkFinished() {
		if(numberOfCollectors == 0 && numberOfUnfinishedTasks == 0) {
			synchronized(listeners) {
				for(WorkingListListener listener : listeners) {
					listener.completedWorklist();
				}
			}
		}
	}

	/**
	 * Listens for events fired by the tasks it contains.
	 */
	public void stateChange(final @Nonnull Task task,
			                final @Nonnull State oldState,
			                final @Nonnull State newState) {
		boolean sourceIsFinished = oldState == State.FINISHED
				|| oldState == State.ABORTED
				|| oldState == State.ERROR;
		boolean targetIsFinished = newState == State.FINISHED
				|| newState == State.ABORTED
				|| newState == State.ERROR;

		synchronized (tasks) {
			if (sourceIsFinished && !targetIsFinished) {
				numberOfUnfinishedTasks += 1;
			} else if (!sourceIsFinished && targetIsFinished) {
				numberOfUnfinishedTasks -= 1;
			}
		}
		
		checkFinished();
	}

	public int getNumberOfTasks() {
		return tasks.size();
	}
	
	public @Nonnull Task getTask(final @Nonnegative int index) {
		assert index >= 0 && index < tasks.size();
		
		return tasks.get(index);
	}

	public int indexOf(final @Nonnull Task task) {
		assert tasks.contains(task);
		
		return tasks.indexOf(task);
	}

}
