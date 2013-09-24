package com.nl.tudelft.rdfgearsUI.client.Dia;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import com.google.gwt.dom.client.Element;

public abstract class PathRenderer {
	abstract void moveCanvasTo(int x, int y);
	abstract void resizeCanvas(int w, int h);
	/**
	 * render a bezier line
	 * @param line[0] start x
	 * @param line[1] start y
	 * @param line[2] control point 1 x
	 * @param line[3] control point 1 y
	 * @param line[4] control point 2 x
	 * @param line[5] control point 2 y
	 * @param line[6] end point x
	 * @param line[7] end point y
	 */
	abstract void renderBezierLine (double [] line);
	
	abstract void draw(Element container);
	abstract void changeColor(String color);
	abstract void remove();
	abstract Element getElement();
	abstract void changeColor();
}
