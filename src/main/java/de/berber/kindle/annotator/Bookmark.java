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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 * A bookmark is a page marker. Therefore it needs no additional data.
 * 
 * @author Bernhard J. Berger
 */
public class Bookmark extends Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Bookmark.class);
	
	public Bookmark(final CompositeConfiguration cc, int page) {
		super(cc, "bookmark", page);
	}

	@Override
	protected PDAnnotation toPDAnnotation(final PDDocumentOutline documentOutline, final PDPage page) {
		LOG.info("Creating bookmark");
		PDOutlineItem bookmarks = documentOutline.getFirstChild();
		
		while(bookmarks != null) {
			if("Bookmarks".equals(bookmarks.getTitle())) {
				break;
			}
			
			bookmarks = bookmarks.getNextSibling();
		}
		
		if(bookmarks == null) {
			bookmarks = new PDOutlineItem();
			bookmarks.setTitle("Bookmarks");
			documentOutline.appendChild(bookmarks);
		}

		final PDOutlineItem bookmark = new PDOutlineItem();
		bookmark.setTitle("Bookmark on page " + getPage());
		bookmark.setDestination(page);
		bookmarks.appendChild(bookmark);
		
		return null;
	}
}
