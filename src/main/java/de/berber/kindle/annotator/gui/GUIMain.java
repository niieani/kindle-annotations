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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.CompositeConfiguration;

import de.berber.kindle.annotator.AbstractMain;
import de.berber.kindle.annotator.Options;
import de.berber.kindle.annotator.controller.WorkQueue;
import de.berber.kindle.annotator.model.WorkingList;

/**
 * Graphical user interface.
 * 
 * @author Bernhard J. Berger
 */
public class GUIMain extends AbstractMain {

	public GUIMain(Options options, CompositeConfiguration cc) {
		super(options, cc);
	}
	
	public void run() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
			    	final WorkingList model    = new WorkingList();
			    	new WorkQueue(cc, model);
			    	final MainWindow view      = new MainWindow(model);

					view.run();
			}});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
