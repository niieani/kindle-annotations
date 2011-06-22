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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;

/**
 * A marking is a colored area on a PDF page.
 * 
 * @author Bernhard J. Berger
 */
public class Marking extends Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Marking.class);
	
	private final double leftXPositionFactor;
	private final double lowerYPositionFactor;
	private final double rightXPositionFactor;
	private final double upperYPositionFactor;

	@SuppressWarnings("unused")
	private int page2; // TODO Check markings with page != page2

	/**
	 * If the marking is merged with a comment it will be stored here to get
	 * the comments text.
	 */
	private Comment comment = null;
	
	/**
	 * A marking starts on {@code page1} at location {@code x1}/{@code y1} and
	 *   ends on on {@code page2} at location {@code x2}/{@code y2}
	 */
	public Marking(final @Nonnull CompositeConfiguration cc,
			       final @Nonnegative int page1,
			       double x1, double y1,
			       final @Nonnegative int page2,
			       double x2, double y2) {
		super(cc, "markings", page1);
		this.page2 = page2;
		
		// check factor values
		checkFactorValue(x1);
		checkFactorValue(y1);
		checkFactorValue(x2);
		checkFactorValue(y2);
		
		this.leftXPositionFactor  = x1;
		this.lowerYPositionFactor = y1;
		this.rightXPositionFactor = x2;
		this.upperYPositionFactor = y2;
	}

	@Override
	protected PDAnnotation toPDAnnotation(final PDDocumentOutline documentOutline, final PDPage page)  {
		LOG.info("Creating marking " + leftXPositionFactor + "/" + lowerYPositionFactor + " -> " + rightXPositionFactor + "/" + upperYPositionFactor);

		// create highlighted area
		final PDGamma pdColor = getColor();
		// final PDFont font = PDType1Font.HELVETICA_BOLD;
		// float textHeight = font.getFontHeight("Hg".getBytes(), 0, 2);
		
		final PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
		txtMark.setColour(pdColor);
		txtMark.setConstantOpacity(opacity);
		
		if(comment != null) {
			// set comment if available
			txtMark.setContents(comment.getText());
		}

		// Set the rectangle containing the markup
		final PDRectangle cropBox = page.getTrimBox();
		
		final PDRectangle position = new PDRectangle();
        position.setLowerLeftX ((float)(cropBox.getLowerLeftX() + leftXPositionFactor * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
        position.setUpperRightX((float)(cropBox.getLowerLeftX() + rightXPositionFactor * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));

        position.setLowerLeftY ((float)(cropBox.getUpperRightY() - (lowerYPositionFactor + ((upperYPositionFactor - lowerYPositionFactor == 0.0) ? 0.025 : 0.00)) * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
        position.setUpperRightY((float)(cropBox.getUpperRightY() - (upperYPositionFactor) * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));

		txtMark.setRectangle(position);
		// work out the points forming the four corners of the annotations
		// set out in anti clockwise form (Completely wraps the text)
		// OK, the below doesn't match that description.
		// It's what acrobat 7 does and displays properly!

        float[] quads = new float[8];
        
		quads[0] = position.getLowerLeftX();   // x1
		quads[1] = position.getUpperRightY();  // y1
		quads[2] = position.getUpperRightX();  // x2
		quads[3] = position.getUpperRightY();  // y2
		quads[4] = position.getLowerLeftX();   // x3
		quads[5] = position.getLowerLeftY();   // y3
		quads[6] = position.getUpperRightX();  // x4
		quads[7] = position.getLowerLeftY();   // y5

		txtMark.setQuadPoints(quads);
		
		return txtMark;
	}
	
	/**
	 * Adds the comment to the marking.
	 */
	public void addComment(final Comment comment) {
		this.comment  = comment;
	}

	public double getRightXPositionFactor() {
		return rightXPositionFactor;
	}

	public double getUpperYPositionFactor() {
		return upperYPositionFactor;
	}
}
