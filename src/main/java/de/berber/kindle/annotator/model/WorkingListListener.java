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
 * A working list listener listens for events produced by the working list.
 *
 * @author Bernhard J. Berger
 */
public interface WorkingListListener {
	/**
	 * Informs the clients that {@code task} was added to the list of current
	 * tasks.
	 * 
	 * @param task The task that was added.
	 */
	public void taskAdded(final @Nonnull Task task);
	
	/**
	 * Informs the clients about a complete model reset.
	 */
	public void modelCleared();

	/**
	 * Informs clients about the end of work.
	 */
	public void completedWorklist();
}
