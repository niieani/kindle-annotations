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
package de.berber.kindle.annotator.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

import de.berber.kindle.annotator.lib.PDFAnnotator;
import de.berber.kindle.annotator.model.State;
import de.berber.kindle.annotator.model.Task;
import de.berber.kindle.annotator.model.WorkingList;
import de.berber.kindle.annotator.model.WorkingListListener;

/**
 * The work queue administrates the worker threads and feeds them with work.
 *   Currently there are as much thread as there are processors. The WorkQueue
 *   will be informed about new tasks by the model.
 *   
 * @author Bernhard J. Berger
 */
public class WorkQueue implements WorkingListListener {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(WorkQueue.class);
			
	private class Converter implements Runnable {
		private final Task task;
		private final CompositeConfiguration cc;

		Converter(final Task task, CompositeConfiguration cc) {
			this.task = task;
			this.cc = cc;
		}

		public void run() {
			LOG.info("Converting " + task);
			
			try {
				switch (task.getState()) {
				case WAITING:
					task.setState(State.RUNNING);
					
					final PDFAnnotator annotator = new PDFAnnotator(cc,
							task.getInputFile().toString(),
							task.getOutputFile().toString());
					
					if(annotator.run()) {
						task.setState(State.FINISHED);
					} else {
						task.setState(State.ERROR);
					}
					break;
	
				case ERROR:
				case RUNNING:
				case FINISHED:
					LOG.error("Invalid task state " + task.getState());
					break;
	
				case ABORTED:
					// user selected abort -> skip this file
					break;
				}
			} catch(final Exception e) {
				LOG.error("Error while converting " + task);
				task.setState(State.ERROR);
			}
		}
	}

	/**
	 * Configuration file
	 */
	private final CompositeConfiguration configuration;
	
	/**
	 * The executor service implements the real work balancing.
	 */
	private ExecutorService executor;
	
	/**
	 * Creates a new work queue that waits for work packages.
	 */
	public WorkQueue(final @Nonnull CompositeConfiguration cc, final @Nonnull WorkingList model) {
		this.configuration = cc;
		
		model.addListener(this);
		
		start();
	}
	
	/**
	 * Startup the executors
	 */
	private void start() {
        final int cpuCount = Runtime.getRuntime().availableProcessors();
        LOG.info("Starting " + cpuCount + " threads.");
        
        executor = Executors.newFixedThreadPool(cpuCount);
	}
	
	/**
	 * Stops the executors.
	 */
	public void stop() {
        executor.shutdown();
        while(!executor.isTerminated()) {
        	try {
        		Thread.sleep(500);
            } catch (InterruptedException e) {
            	LOG.error("Thread was interrupted");
            }
        }
	}
	
	/**
	 * Listen for added tasks and submits a new work package.
	 */
	public void taskAdded(Task task) {
        executor.submit(new Converter(task, configuration));
	}

	/**
	 * A model clear resets the executor.
	 */
	public void modelCleared() {
		stop();
		start();
	}

	/**
	 * Nothing to do in this case.
	 */
	public void completedWorklist() {
	}
}
