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


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class RGMenuBar extends Composite {
	private RGCanvas canvas;
	private static RGMenuBarUiBinder uiBinder = GWT
			.create(RGMenuBarUiBinder.class);

	interface RGMenuBarUiBinder extends UiBinder<Widget, RGMenuBar> {
	}
	@UiField
	MenuBar menubar;
	
	@UiField MenuItem newWf;
	@UiField MenuItem newWfFromSource;
	@UiField MenuItem saveWf;
	@UiField MenuItem exportWf;
	//@UiField MenuItem devTest;
	@UiField MenuItem saveRunWf;
	@UiField MenuItem showHideNodeId;
	@UiField MenuItem viewLastSavedWfSource;
	@UiField MenuItem viewPortType;
	
	public RGMenuBar(RGCanvas _canvas) {
		canvas = _canvas;
		initWidget(uiBinder.createAndBindUi(this));
		menubar.getElement().addClassName("menuBar-horizontal");
		
		newWf.setCommand(new Command(){
			public void execute() {
				canvas.createNewWorkflow(canvas.createUniqueWorkflowId(), "New-Workflow");
			}
			
		});
		newWfFromSource.setCommand(new Command(){
			public void execute() {
				canvas.createNewWorkflowFromSource();
			}
			
		});
		saveWf.setCommand(new Command(){
			public void execute() {
				canvas.saveWorkflow();
			}
			
		});
		
		saveRunWf.setCommand(new Command(){
			public void execute(){
				canvas.saveAndRun();
			}
		});
		
		showHideNodeId.setCommand(new Command(){
			public void execute(){
				canvas.showHideNodeId();
			}
		});
		
		viewLastSavedWfSource.setCommand(new Command(){
			public void execute() {
				canvas.viewOriginalWorkflowSource(canvas.getActiveWorkflow().getId());
			}
			
		});
		
		exportWf.setCommand(new Command(){
			public void execute() {
				canvas.displayFormattedXml(canvas.getActiveWorkflow().exportToXml());
			}
			
		});
		
		viewPortType.setCommand(new Command(){
			public void execute() {
				canvas.viewActiveWorkflowPortType();
			}
			
		});
		
//		devTest.setCommand(new Command(){
//			public void execute() {
//				canvas.devTest();
//			}
//			
//		});
		
	}
	
	

}
