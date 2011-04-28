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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

/**
 * A marking is a colored text area.
 * 
 * @author Bernhard J. Berger
 */
public class Marking extends Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Marking.class);
	private final double leftXFactor;
	public double getRightXFactor() {
		return rightXFactor;
	}

	public double getUpperYFactor() {
		return upperYFactor;
	}

	private final double lowerYFactor;
	private final double rightXFactor;
	private final double upperYFactor;
	private int page2;
	private Comment comment = null;
	
	public Marking(final CompositeConfiguration cc, int page1, double x1, double y1, int page2, double x2, double y2) {
		super(cc, "markings", page1);
		this.page2 = page2;
		
		checkFactorValue(x1);
		checkFactorValue(y1);
		checkFactorValue(x2);
		checkFactorValue(y2);
		
		this.leftXFactor  = x1;
		this.lowerYFactor = y1;
		this.rightXFactor = x2;
		this.upperYFactor = y2;
	}

	@Override
	protected PDAnnotation toPDAnnotation(final PDPage page)  {
		LOG.info("Creating marking " + leftXFactor + "/" + lowerYFactor + " -> " + rightXFactor + "/" + upperYFactor);

		try {
			final PDGamma pdColor = getColor();
			
			final PDFont font = PDType1Font.HELVETICA_BOLD;
			float textHeight = font.getFontHeight("Hg".getBytes(), 0, 2);
			
			final PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
			txtMark.setColour(pdColor);
			txtMark.setConstantOpacity(opacity);   // Make the highlight 20% transparent
			
			if(comment != null) {
				txtMark.setContents(comment.getText());
			}
	
			// Set the rectangle containing the markup
			final PDRectangle cropBox = page.getTrimBox();
			
			final PDRectangle position = new PDRectangle();
	        position.setLowerLeftX ((float)(cropBox.getLowerLeftX() + leftXFactor * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
	        position.setUpperRightX((float)(cropBox.getLowerLeftX() + rightXFactor * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
	
	        position.setLowerLeftY ((float)(cropBox.getUpperRightY() - (lowerYFactor + ((upperYFactor - lowerYFactor == 0.0) ? 0.025 : 0.00)) * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
	        position.setUpperRightY((float)(cropBox.getUpperRightY() - (upperYFactor) * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
	
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
		} catch(IOException e) {
			return null;
		}
	}
	
	public void addComment(final Comment comment) {
		this.comment  = comment;
	}
}
