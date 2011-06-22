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
package de.berber.kindle.annotator;

import java.io.File;

import javax.annotation.Nonnull;

import de.berber.kindle.annotator.controller.WorkCollector;
import de.berber.kindle.annotator.model.Task;
import de.berber.kindle.annotator.model.WorkingList;
import de.berber.kindle.annotator.model.WorkingListListener;

/**
 * The batch command line main program.
 * 
 * TODO Use the default data model framework to avoid duplications.
 * 
 * @author Bernhard J. Berger
 */
public class BatchMain extends AbstractMain implements WorkingListListener {

	private boolean worklistFinished = false;

	public BatchMain(final @Nonnull Options options, final @Nonnull WorkingList model) {
		super(options, model);
		
		model.addListener(this);
	}

	@Override
	public void run() {
		final File inputFile = new File(options.input); // TODO Check 
		final File outputFile = new File(options.output); // TODO Check 
		final WorkCollector collector = new WorkCollector(inputFile, outputFile, model);

		collector.run();
		
		while(!worklistFinished) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void taskAdded(Task task) {
	}

	public void modelCleared() {
	}

	public void completedWorklist() {
		worklistFinished = true;
	}
}