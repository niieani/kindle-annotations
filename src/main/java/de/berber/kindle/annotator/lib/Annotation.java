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

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;

/**
 * Parent of all annotations. An annotation belongs to a particular page and can
 * be converted into a {@link PDAnnotation}.
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

	/**
	 * @return The page of the annotation
	 */
	public int getPage() {
		return page;
	}

	/**
	 * The default color of all annotations
	 */
	protected String color = "#0000FF";

	/**
	 * The default opacity of all annotations.
	 */
	protected float opacity = 0.2f;

	/**
	 * Creates a new annotation with a {@code configuration}, a {@code prefix}
	 * for configuration lookups and a {@code page} number.
	 */
	protected Annotation(final @Nonnull CompositeConfiguration configuration,
			final @Nonnull String prefix, final @Nonnegative int page) {
		this.page = page;
		color = configuration.getString(prefix + ".color", color);
		opacity = configuration.getFloat(prefix + ".opacity", opacity);
	}

	/**
	 * Checks whether the current belongs to the page {@code currentPageNumber}
	 * and adds the annotation in such an case.
	 * 
	 * @param currentPageNumber
	 *            The page number of {@code page}.
	 * @param documentOutline
	 *            The pdf outline
	 * @param page
	 *            The pdf page.
	 */
	@SuppressWarnings("unchecked")
	public void toPDAnnotation(final @Nonnegative int currentPageNumber,
			final @Nonnull PDDocumentOutline documentOutline,
			final @Nonnull PDPage page) {
		if (this.page != currentPageNumber) {
			return;
		}

		try {
			final List<PDAnnotation> annotations = ((List<PDAnnotation>) page
					.getAnnotations());
			final PDAnnotation annotation = toPDAnnotation(documentOutline,
					page);

			if (annotation != null) {
				annotations.add(annotation);
			}
		} catch (IOException e) {
			LOG.error("Cannot read annotations from PDF.");
		}
	}

	/**
	 * Create a {@link PDAnnotation} for the current annotation.
	 * 
	 * @param documentOutline
	 *            The document outline (needed for bookmarks).
	 * @param page
	 *            The current PDF page.
	 * @return A new annotation or {@code null}.
	 */
	protected abstract @Nullable PDAnnotation toPDAnnotation(
			final @Nonnull PDDocumentOutline documentOutline,
			final @Nonnull PDPage page);

	/**
	 * Checks whether a multiplier is in the allowed page range.
	 */
	protected static final void checkFactorValue(final double factor) {
		if (factor < 0.0 || factor > 1.0) {
			LOG.warn("Factor is out of visible range: " + factor);
		}
	}

	/**
	 * Converts the color string into a PDGamma object.
	 * 
	 * @return
	 */
	protected final PDGamma getColor() {
		assert color.length() == 7 && color.startsWith("#");
		final PDGamma color = new PDGamma();

		color.setR(Integer.parseInt(this.color.substring(1, 3), 16) / 255f);
		color.setG(Integer.parseInt(this.color.substring(3, 5), 16) / 255f);
		color.setB(Integer.parseInt(this.color.substring(5, 7), 16) / 255f);

		return color;
	}
}
