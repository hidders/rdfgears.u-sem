/**
 * Path renderer with HTML5 Canvas
 */
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


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class CanvasPathRenderer extends PathRenderer{
	
	private Canvas canvas = null;
	private Context2d ctx = null;
	private int width, height, posX, posY;
	private String defaultColor = "#3583F2";
	private String lineColor = defaultColor;
	double [] line;
	public CanvasPathRenderer(int width, int height, int x, int y) {	
		canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		resizeCanvas(width, height);
		moveCanvasTo(x, y);
	}
	
	public void moveCanvasTo(int x, int y){
		posX = x; 
		posY = y;
		canvas.getElement().getStyle().setLeft(posX, Style.Unit.PX);
		canvas.getElement().getStyle().setTop(posY, Style.Unit.PX);
	}
	
	public void resizeCanvas(int w, int h){
		width = w;
		height = h;
		canvas.setWidth(width + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceHeight(height);
	}

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
	public void renderBezierLine (double [] _line){
		line = _line;
		ctx.clearRect(0, 0, width, height);
		ctx.beginPath();
		ctx.setLineWidth(3);
		ctx.setStrokeStyle(CssColor.make(lineColor));
		ctx.moveTo(line[0], line[1]);
		ctx.bezierCurveTo(line[2], line[3], line[4], line[5], line[6], line[7]);
		ctx.stroke();
	
	}
	
	public void draw(Element container){
		container.appendChild(canvas.getCanvasElement());
	}
	public void remove(){
		canvas.getCanvasElement().removeFromParent();
		canvas = null;
	}

	@Override
	void changeColor(String color) {
		lineColor = color;
		renderBezierLine(line);
	}
	
	void changeColor(){
		lineColor = defaultColor;
		renderBezierLine(line);
	}
	
	public Element getElement(){
		return canvas.getElement();
	}
	
}
