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

import javax.annotation.Nonnull;

import de.berber.kindle.annotator.model.WorkingList;

/**
 * An abstract main class for GUI and batch version.
 * 
 * @author Bernhard J. Berger
 */
public abstract class AbstractMain {
	/**
	 * The model.
	 */
	protected WorkingList model;

	/**
	 * Commandline options
	 */
	protected Options options;
	
	public AbstractMain(final @Nonnull Options options, final @Nonnull WorkingList model) {
		this.options = options;
		this.model   = model;
	}

	/**
	 * Runs the main process.
	 */
	public abstract void run();
}
