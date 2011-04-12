/*
 *
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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

/**
 * Parent of all annotations. An annotation belongs to a particular page and
 *   can be converted into a {@link PDAnnotation}.
 *   
 * @author Bernhard J. Berger
 */
public abstract class Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Annotation.class);
		
	/**
	 * Zero-based page number of the annotation
	 */
	private final int page;
	
	protected Annotation(int page) {
		this.page = page;
	}
	
	/**
	 * Checks whether the current belongs to the page {@code currentPageNumber}
	 *  and adds the annotation in such an case.
	 * 
	 * @param currentPageNumber The page number of {@code page}.
	 * @param page The pdf page.
	 */
	@SuppressWarnings("unchecked")
	public void toPDAnnotation(int currentPageNumber, PDPage page) {
		if(this.page != currentPageNumber) {
			return;
		}
		
		try {
			final List<PDAnnotation> annotations = ((List<PDAnnotation>)page.getAnnotations());
			final PDAnnotation annotation = toPDAnnotation(page);
			if(annotation != null) {
				annotations.add(annotation);
			}
		} catch(IOException e) {
			LOG.error("Cannot read annotations");
		}
	}

	protected abstract PDAnnotation toPDAnnotation(final PDPage page);
}
