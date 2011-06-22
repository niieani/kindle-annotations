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

import javax.annotation.Nonnull;

/**
 * Registered task listeners will be informed about state changes of a task.
 *
 * @author Bernhard J. Berger
 */
public interface TaskListener {
	/**
	 * Informs the listener about a state change.
	 * 
	 * @param task The task that changed.
	 * @param oldState The old state of the task.
	 * @param newState The new state of the task.
	 */
	public void stateChange(final @Nonnull Task task,
			                final @Nonnull State oldState,
			                final @Nonnull State newState);
}
