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


import com.google.gwt.core.client.JavaScriptObject;

public class UtilJSWrapper {
	private JavaScriptObject editor;
	/**
	 * set a textarea as code mirror sparql query editor
	 * @param editorId
	 */
	public static native JavaScriptObject setAsCMSparqlEditor(String editorId) /*-{
	  return new $wnd.CodeMirror.fromTextArea($doc.getElementById(editorId), {
        mode: "application/x-sparql-query",
        tabMode: "indent",
        matchBrackets: true,
        lineNumbers: true
      });
	}-*/;
	
	/**
	 * set a textarea as code mirror sparql query editor
	 * @param editorId
	 */
	public static native JavaScriptObject setAsCMXmlViewer(String containerId) /*-{
	  return new $wnd.CodeMirror.fromTextArea($doc.getElementById(containerId), {
     	mode: {name: "xml", alignCDATA: true},
        lineNumbers: true,
        readOnly: true
      });
	}-*/;
	
	public static native JavaScriptObject setAsCMXmlEditor(String containerId) /*-{
	  return new $wnd.CodeMirror.fromTextArea($doc.getElementById(containerId), {
   		mode: {name: "xml", alignCDATA: true},
      	lineNumbers: true
    });
	}-*/;
	/**
	 * editor must be CodeMirror object
	 * @param editor
	 * @return
	 */
	public static native String getEditorValue(JavaScriptObject editor) /*-{
		return editor.getValue();
	}-*/;
	
	public static native void setEditorValue(JavaScriptObject editor, String value) /*-{
		editor.setValue(value);
	}-*/;
	
	public static native void setEditorSize(JavaScriptObject editor, int width, int height) /*-{
		editor.getWrapperElement().style.height = height + 'px';
		editor.getScrollerElement().style.height = height + 'px';
		editor.refresh();
	}-*/;
	
	public static native void setFocus(String elementId) /*-{
		$doc.getElementById(elementId).focus();
	}-*/;
}
