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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;

/**
 * A comment is some text added somewhere to the document.
 * 
 * @author Bernhard J. Berger
 */
public class Comment extends Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Comment.class);

	/**
	 * X location factor of the text.
	 */
	private final double xPositionFactor;

	/**
	 * Y location factor of the text.
	 */
	private final double yPositionFactor;

	/**
	 * The text of the comment.
	 */
	private final String text;

	public Comment(final @Nonnull CompositeConfiguration cc,
			       final @Nonnegative int page,
			       double xPositionFactor,
			       double yPositionFactor,
			       final @Nonnull String text) {
		super(cc, "comments", page);

		checkFactorValue(xPositionFactor);
		checkFactorValue(yPositionFactor);

		this.xPositionFactor = xPositionFactor;
		this.yPositionFactor = yPositionFactor;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public double getXPositionFactor() {
		return xPositionFactor;
	}

	public double getYPositionFactor() {
		return yPositionFactor;
	}

	@Override
	protected PDAnnotation toPDAnnotation(
			final @Nonnull PDDocumentOutline documentOutline, final @Nonnull PDPage page) {
		LOG.info("Creating annotation " + xPositionFactor + "/"
				+ yPositionFactor + " -> " + text);

		// Create annotation text with background color
		final PDGamma pdColor = getColor();
		final PDAnnotationText textAnnotation = new PDAnnotationText();
		textAnnotation.setContents(getText());
		textAnnotation.setColour(pdColor);

		// set the text position
		final PDRectangle cropBox = page.getTrimBox();
		final PDRectangle position = new PDRectangle();
		position.setLowerLeftX((float) (cropBox.getLowerLeftX() + xPositionFactor
				* (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
		position.setUpperRightX((float) (cropBox.getLowerLeftX() + xPositionFactor
				* (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));

		position.setUpperRightY((float) (cropBox.getUpperRightY() - yPositionFactor
				* (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
		position.setLowerLeftY((float) (cropBox.getUpperRightY() - yPositionFactor
				* (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));

		textAnnotation.setRectangle(position);

		return textAnnotation;
	}
}
