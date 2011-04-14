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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;

/**
 * A comment is some text added to the document.
 * 
 * @author Bernhard J. Berger
 */
public class Comment extends Annotation {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Comment.class);
	
	private final double xFactor;
	private final double yFactor;
	private final String text;
	
	public Comment(final CompositeConfiguration cc, int page, double x, double y, final String text) {
		super(cc, "comments", page);
		
		checkFactorValue(x);
		checkFactorValue(y);
		
		this.xFactor = x;
		this.yFactor = y;
		this.text = text;
}

	public String getText() {
		return text;
	}

	@Override
	protected PDAnnotation toPDAnnotation(final PDPage page) {
		LOG.info("Creating annotation " + xFactor + "/" + yFactor + " -> " + text);
		
		final PDGamma pdColor = getColor();

		final PDAnnotationText textAnnotation = new PDAnnotationText();
		textAnnotation.setContents(getText());
		textAnnotation.setColour(pdColor);
		
		final PDRectangle cropBox = page.getTrimBox();
		final PDRectangle position = new PDRectangle();
        position.setLowerLeftX ((float)(cropBox.getLowerLeftX() + xFactor * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
        position.setUpperRightX((float)(cropBox.getLowerLeftX() + xFactor * (cropBox.getUpperRightX() - cropBox.getLowerLeftX())));
        
        position.setUpperRightY((float)(cropBox.getUpperRightY() - yFactor * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
        position.setLowerLeftY ((float)(cropBox.getUpperRightY() - yFactor * (cropBox.getUpperRightY() - cropBox.getLowerLeftY())));
        
        textAnnotation.setRectangle(position);
        
        return textAnnotation;
	}
}
