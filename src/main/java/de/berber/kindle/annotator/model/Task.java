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
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A task is a work package that contains all necessary data to add annotations
 *   for a PDF file and store it back to disk.
 *   
 * @author Bernhard J. Berger
 */
public class Task {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Task.class);
	
	/**
	 * The input PDF-file.
	 */
	private final File inputFile;
	
	/**
	 * The target file.
	 */
	private final File outputFile;
	
	/**
	 * Current task state (initial state is waiting)
	 */
	private State state = State.WAITING;
	
	/**
	 * Create a new task for conversion of {@code inputFile} to
	 *   {@code outputFile}.
	 */
	public Task(final File inputFile, final File outputFile) {
		this.inputFile  = inputFile;
		this.outputFile = outputFile;
		
	}
	
	/**
	 * Set of listeners.
	 */
	private Set<TaskListener> listeners = new HashSet<TaskListener>();
	
	/**
	 * Adds a new listener to the set of listeners.
	 * 
	 * @param listener The listener to add.
	 */
	public final void addListener(final TaskListener listener) {
		LOG.info("Adding listener " + listener + " for task " + this);
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Removes the listener from the set of listeners.
	 * 
	 * @param listener The listener to remove.
	 */
	public final void removeListener(final TaskListener listener) {
		LOG.info("Removing listener " + listener + " from task " + this);
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * @return The current task state.
	 */
	public final State getState() {
		return state;
	}

	/**
	 * Change the task state to {@code state}.
	 * 
	 * @param state The new state
	 */
	public synchronized final void setState(final State state) {
		LOG.info("Setting state for " + this + " to " + state);
		
		if(state == getState()) {
			return; // no state change necessary
		}
		
		final State oldState = getState();
		this.state = state;
		
		synchronized(listeners) {
			for(TaskListener listener : listeners) {
				listener.stateChange(this, oldState, state);
			}
		}

	}

	/**
	 * @return The task's input file
	 */
	public final File getInputFile() {
		return inputFile;
	}

	/**
	 * @return The task's output file
	 */
	public final File getOutputFile() {
		return outputFile;
	}

	@Override
	public String toString() {
		return "Task <" + state + " : "+ inputFile.getName() + " -> " + outputFile.getName() + ">";
	}
}
