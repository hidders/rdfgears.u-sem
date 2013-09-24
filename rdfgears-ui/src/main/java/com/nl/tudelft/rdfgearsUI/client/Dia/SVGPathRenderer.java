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
import com.google.gwt.dom.client.Style;

public class SVGPathRenderer extends PathRenderer {
	private Element canvas = null;
	private Element path = null;
	private static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
	private double[] line;
	private String defaultColor = "#3583F2";
	private String lineColor = defaultColor;
	
	public SVGPathRenderer(int width, int height, int x, int y){
		canvas = createElementNS(SVG_NAMESPACE, "svg");
		moveCanvasTo(x, y);
		resizeCanvas(width, height);
		path = createElementNS(SVG_NAMESPACE, "path");
		path.setAttribute("stroke-width", ""+3);
		path.setAttribute("stroke", lineColor);
		path.setAttribute("fill", "none");
		canvas.appendChild(path);
		
	}
	
	void moveCanvasTo(int x, int y) {
		canvas.setAttribute("style", "top:" + y + "px;left:"+ x +"px;");
	}

	void resizeCanvas(int w, int h) {
		canvas.setAttribute("width", ""+w);
		canvas.setAttribute("height", ""+h);
		canvas.getStyle().setWidth(w, Style.Unit.PX);
		canvas.getStyle().setHeight(h, Style.Unit.PX);
	}

	void renderBezierLine(double[] _line) {
		line = _line;
		path.setAttribute("d", "M "+line[0]+" "+line[1]+
				" C "+line[2]+" "+line[3]+" "+
				line[4]+" "+line[5]+" "+
				line[6]+" "+line[7]);			
	}

	void draw(Element container) {
		container.appendChild(canvas);
	}

	void remove() {
		canvas.removeFromParent();
	}
	
	private static native Element createElementNS(String svgNS, String name)/*-{ 
    	return document.createElementNS(svgNS, name);
	}-*/;

	@Override
	void changeColor(String color) {
		lineColor = color;
		path.setAttribute("stroke", lineColor);
		renderBezierLine(line);
	}

	@Override
	Element getElement() {
		return canvas;
	}

	@Override
	void changeColor() {
		lineColor = defaultColor;
		path.setAttribute("stroke", lineColor);
		renderBezierLine(line);
	}
	
}
