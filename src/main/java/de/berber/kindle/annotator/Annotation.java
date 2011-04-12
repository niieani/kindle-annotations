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

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

public abstract class Annotation {
	private final int page;
	
	protected Annotation(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return this.page;
	}
	
	@SuppressWarnings("unchecked")
	public void toPDAnnotation(int currentPageNumber, PDPage page) throws IOException {
		if(this.page != currentPageNumber) {
			return;
		}
		
		final PDAnnotation annotation = toPDAnnotation(page);
		if(annotation != null) {
			((List<PDAnnotation>)page.getAnnotations()).add(annotation);
		}
	}

	protected abstract PDAnnotation toPDAnnotation(final PDPage page);
}
