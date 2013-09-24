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


import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class RGTabBar extends Composite{

	private static RGTabBarUiBinder uiBinder = GWT
			.create(RGTabBarUiBinder.class);

	interface RGTabBarUiBinder extends UiBinder<Widget, RGTabBar> {
	}
	
	@UiField
	HTMLPanel tabContainer;
	
	private RGCanvas canvas;
	private Map <String, RGTab> tabs = new HashMap <String, RGTab>();
	private RGTab activeTab;
	public RGTabBar(RGCanvas c) {
		canvas = c;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public RGTab addTab(RGWorkflow wf){
		
		if(!tabs.containsKey(wf.getId())){
			RGTab newTab = new RGTab(wf, canvas);
			tabs.put(wf.getId(), newTab);
			tabContainer.getElement().appendChild(newTab.getElement());
			newTab.enableEventHandler();
			setActiveWorkflowTab(wf.getId());
			return newTab;
		}
		
		return null;
	}
	
	public void removeWorkflowTab(String wfId){
		if(tabs.containsKey(wfId)){
			tabs.get(wfId).remove();
			tabs.remove(wfId);
		}
	}
	
	public void setActiveWorkflowTab(String wfId){
		RGTab newActiveTab = tabs.get(wfId);
		if(newActiveTab != null){
			if(activeTab != null){
				activeTab.setActive(false);
			}
			activeTab = newActiveTab;
			activeTab.setActive(true);
		}else{
//			Log.debug("wf tab not found:" + wfId);
		}
		//setActive workflow tab
	}
	
	public void refreshTabTitleFor(String wfId){
		if(tabs.containsKey(wfId)){
			tabs.get(wfId).refresh();
		}
	}
	
	public boolean replaceOpenedWorkflowId(String oldId, String newId){
		if(tabs.containsKey(oldId) && !tabs.containsKey(newId)){
			RGTab ref = tabs.get(oldId);
			tabs.remove(oldId);
			tabs.put(newId, ref);
			Log.debug("new wf Id updated on tabbar:" + newId);
			return true;
		}
		
		return false;
	}
}
