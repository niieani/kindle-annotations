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

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

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
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;

	public Marking(int page, double x1, double y1, double x2, double y2) {
		super(page);
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
	}

	@Override
	protected PDAnnotation toPDAnnotation(final PDPage page)  {
		LOG.info("Creating marking " + x1 + "/" + y1 + " -> " + x2 + "/" + y2);
		/* TODO Fix position translation
		try {
			final PDGamma colourBlue = new PDGamma();
			colourBlue.setB(1);
			
			final PDFont font = PDType1Font.HELVETICA_BOLD;
			float textHeight = font.getFontHeight("Hg".getBytes(), 0, 2);
			
			final PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
			txtMark.setColour(colourBlue);
			txtMark.setConstantOpacity((float)0.2);   // Make the highlight 20% transparent
	
			// Set the rectangle containing the markup
			final PDRectangle cropBox = page.getCropBox();
			
			final PDRectangle position = new PDRectangle();
	        position.setLowerLeftX ((float)(cropBox.getLowerLeftX() + x1 * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
	        position.setUpperRightX((float)(cropBox.getLowerLeftX() + x2 * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
	
	        position.setLowerLeftY ((float)(cropBox.getUpperRightY() - (y1) * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())  - textHeight * 1000));
	        position.setUpperRightY((float)(cropBox.getUpperRightY() - (y2) * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
	
			txtMark.setRectangle(position);
			// work out the points forming the four corners of the annotations
			// set out in anti clockwise form (Completely wraps the text)
			// OK, the below doesn't match that description.
			// It's what acrobat 7 does and displays properly!
	
	        float[] quads = new float[8];
	
			quads[0] = position.getLowerLeftX();  // x1
			quads[1] = position.getUpperRightY(); // y1
			quads[2] = position.getUpperRightX(); // x2
			quads[3] = quads[1]; // y2
			quads[4] = quads[0];  // x3
			quads[5] = position.getLowerLeftY(); // y3
			quads[6] = quads[2]; // x4
			quads[7] = quads[5]; // y5
	
			txtMark.setQuadPoints(quads);
			//txtMark.setContents("Hallo Welt");
			
			return txtMark;
		} catch(IOException e) {
			return null;
		}
		*/
		return null;
	}
}
