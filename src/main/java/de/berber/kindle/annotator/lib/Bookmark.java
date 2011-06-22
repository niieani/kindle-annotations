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
package de.berber.kindle.annotator.lib;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 * A bookmark is a simple page marker. No additional data needed. The
 * 	bookmarks are added to the documents outline. The outline may contain a
 *  list of contents therefore a new entry "Bookmarks" will be added and all
 *  bookmarks will be children of this entry.
 * 
 * @author Bernhard J. Berger
 */
public class Bookmark extends Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Bookmark.class);
	
	/**
	 * Creates a new bookmark on page {@code page}.
	 */
	public Bookmark(final @Nonnull CompositeConfiguration cc, final @Nonnegative int page) {
		super(cc, "bookmark", page);
	}

	@Override
	protected PDAnnotation toPDAnnotation(final @Nonnull PDDocumentOutline documentOutline, final @Nonnull PDPage page) {
		LOG.info("Creating bookmark");
		
		final String OUTLINE_ENTRY_NAME = "Bookmarks";

		// search for an outline entry called Bookmarks
		PDOutlineItem bookmarks = documentOutline.getFirstChild();
		
		while(bookmarks != null) {
			if(OUTLINE_ENTRY_NAME.equals(bookmarks.getTitle())) {
				break;
			}
			
			bookmarks = bookmarks.getNextSibling();
		}
		
		// if we did not found an entry we have to add a new one
		if(bookmarks == null) {
			bookmarks = new PDOutlineItem();
			bookmarks.setTitle(OUTLINE_ENTRY_NAME);
			documentOutline.appendChild(bookmarks);
		}

		// crate the bookmark entry
		final PDOutlineItem bookmark = new PDOutlineItem();
		bookmark.setTitle("Bookmark on page " + getPage());
		bookmark.setDestination(page);
		bookmarks.appendChild(bookmark);
		
		return null;
	}
}
