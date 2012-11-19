/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.deri.pipes.endpoints;
import java.util.ArrayList;
import java.util.List;

import org.deri.pipes.core.Engine;
import org.deri.pipes.store.PipeStore;
import org.deri.pipes.ui.FunctionLoader;
import org.deri.pipes.ui.PipeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;


public class PipeListRenderer implements RowRenderer {
	final Logger logger = LoggerFactory.getLogger(PipeListRenderer.class);
	Engine engine = Engine.defaultEngine();
	Textbox checkPassText;
	Window checkPassWin=null;
	Button checkPassEnter,checkPassCancel;
	CheckPassListener checkPassListener;
	PipeEditor wsp;
	private String fixedPipeGridId;
	private String brokenPipeGridId;
	public PipeListRenderer(PipeEditor wsp,Window checkPassWin,String fixedPipeGridId, String brokenPipeGridId){
		this.wsp=wsp;
		this.checkPassWin=checkPassWin;
		this.fixedPipeGridId = fixedPipeGridId;
		this.brokenPipeGridId = brokenPipeGridId;
		init();
	}
	public void init(){
		checkPassListener=new CheckPassListener();
		checkPassText= new Textbox();
		//checkPassText.addEventListener("onChange",checkPassListener);
		checkPassText.setParent(checkPassWin);    		    			
		checkPassEnter= new Button("Enter");
		checkPassEnter.addEventListener("onClick",checkPassListener);
		checkPassEnter.setParent(checkPassWin);
		checkPassCancel= new Button("Cancel");		
		checkPassCancel.addEventListener("onClick",new EventListener(){
			public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
				checkPassWin.setVisible(false);
			}
		});
		checkPassCancel.setParent(checkPassWin);
	}
	
	/** modified by Eric to respect existing newline chars */
	String justify(String str,int length){
		if(str == null){
			return "";
		}
		StringBuffer result=new StringBuffer();
		
		String[] lines = str.split("\n");
		for (int j=0; j<lines.length; j++){
			String line = lines[j];
			
			String[] words= line.split(" ");
			int count=0;
			for(int i=0;i<words.length;i++){
				if(count+words[i].length()>length){
					result.append("<br />\n");
					count=0;
				}else{
					result.append(" ");
				}
				result.append(words[i]);
				count+=words[i].length();
			}
			
			result.append("<br />\n");
			
		}
		
		
		
		return result.toString();
	}
	
    public void render(Row row, Object data) {
      row.setValign("top");	
      String pipeid=((PipeConfig)data).getId();
      String displayName = pipeid.startsWith("broken/")?pipeid.substring("broken/".length()):pipeid;
      Button pipeButton = new Button();
      pipeButton.setHref("../rdfgears-rest/user/input/"+pipeid);
      pipeButton.setLabel(displayName);
      pipeButton.setId("workflow:"+pipeid);
      pipeButton.setDraggable("true");
      pipeButton.setTooltiptext("Click to execute this workflow");
//      Html pipeLink=new Html("<a href='./pipes/?id="+pipeid+"'>"+displayName+"</a><br />" +
      
      
      String justified = justify(((PipeConfig)data).getName(),30); // insert <br/> every 30 chars
      
       Html pipeLink=new Html("<br />" + justified   );
       Vbox vbox = new Vbox();
       vbox.appendChild(pipeButton);
       vbox.appendChild(pipeLink);
      row.appendChild(vbox);
      
      Menubar menuBar =new Menubar();
      menuBar.setWidth("65px");
      Menu action=new Menu("actions");
      Menupopup popup=new Menupopup();
      //popup.setParent(action);
      action.appendChild(popup);
       
      Menuitem  copy2Editor=new Menuitem("Copy");
      copy2Editor.addEventListener("onClick", new PipeListener(pipeid,PipeListener.CLONE));
      popup.appendChild(copy2Editor);
      if(!(pipeid.equalsIgnoreCase("nested")||pipeid.equalsIgnoreCase("simplemix")||pipeid.equalsIgnoreCase("transform"))){
    	  Menuitem edit=new Menuitem("Edit");
	      edit.addEventListener("onClick", new PipeListener(pipeid,PipeListener.EDIT));
	      popup.appendChild(edit);
    	  Menuitem delete=new Menuitem("Delete");
	      delete.addEventListener("onClick", new PipeListener(pipeid,PipeListener.DELETE));
	      popup.appendChild(delete);
      }
      Menuitem  debug=new Menuitem("Debug");
      debug.addEventListener("onClick", new PipeListener(pipeid,PipeListener.DEBUG));
      popup.appendChild(debug);
      if(!pipeid.startsWith("broken/")){
          Menuitem  broken=new Menuitem("Mark as broken");
          broken.addEventListener("onClick", new PipeListener(pipeid,PipeListener.BROKEN));
          popup.appendChild(broken);
         // broken.setParent(popup);    	  
      }else{
          Menuitem  fixed =new Menuitem("Mark as fixed");
          fixed.addEventListener("onClick", new PipeListener(pipeid,PipeListener.FIXED));
          popup.appendChild(fixed);
    	  
      }
      action.setStyle("color: red;font-weight: bold;");
      action.setParent(menuBar);
      row.appendChild(menuBar);
      row.setNowrap(true);
    }
    public class PipeListener implements EventListener{
        public static final int CLONE=1;
        public static final int EDIT=2;
        public static final int DELETE=3;
        public static final int DEBUG=4;
        public static final int BROKEN=5;
        public static final int FIXED=6;
    	private String pipeid=null;
    	private int type;
    	public PipeListener(String pipeid,int type){
    		this.pipeid=pipeid;
    		this.type=type;
    	}
    	public String getBaseUrl(){
    		Execution exec=Executions.getCurrent();
    		return "http://"+exec.getServerName()+":"+exec.getServerPort()+exec.getContextPath();
    	}
    	public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
 	    	PipeStore pipeStore = engine.getPipeStore();
			PipeConfig config = pipeStore.getPipe(pipeid);
			
			Textbox filterbox = (Textbox) wsp.getFellow("filterWorkflow");
			String queryString = filterbox.getValue();
			
    		switch(type){
    		    case CLONE:
    		    		wsp.clone(pipeid);
    		    	break;
    		    case DELETE:
    		    	if(config!= null && config.getPassword()!=null){   
    		    		try{
    		    			checkPassListener.setPipeId(pipeid);
    		    			checkPassWin.doModal();    		    			
    	    			}
    	    			catch(java.lang.InterruptedException e){
    	    				checkPassText.setParent(null);
    	    			} 
    		    	}
    		    	else{
    		    		try{
    		    			if (Messagebox.show("Are you sure want delete this PipeConfig?", "Delete?", Messagebox.YES | Messagebox.NO,
        		    				Messagebox.QUESTION) == Messagebox.YES) {
    		    				pipeStore.deletePipe(pipeid);
        		    		}
    	    			}
    	    			catch(java.lang.InterruptedException e){
    	    			}    		    		
    		    	}
    				refreshPipeLists(queryString);
		    		break;	
    		    case EDIT:
    		    	wsp.edit(pipeid);
		    		break;
    		    case DEBUG:
    		    	System.out.println("PipeListRenderer DEBUG firing  "); // by eric
					wsp.debug(config.getSyntax());
    	
		    		break;	    		    	
    		    case BROKEN:  
    		    	if(!config.getId().startsWith("broken/")){
    		    		String newId = "broken/"+config.getId();
    		    		renamePipe(pipeStore,config,newId);
    		    		refreshPipeLists(queryString);
    		    	}
    	
		    		break;	    		    	
    		    case FIXED:
    		    	if(config.getId().startsWith("broken/")){
    		    		String newId = config.getId().substring("broken/".length());
    		    		renamePipe(pipeStore,config,newId);
    		    		refreshPipeLists(queryString);
    		    	}
    	
		    		break;
		    	default:
    		}
    		
  		  
  	    }
    	
		/**
		 * @param pipeStore
		 * @param config
		 * @param newId
		 */
		private void renamePipe(PipeStore pipeStore, PipeConfig config,
				String newId) {
			String oldId = config.getId();
			if(pipeStore.contains(newId)){
				newId+="."+System.currentTimeMillis();
			}
			config.setId(newId);
    		if(pipeStore.save(config)){
    			logger.info("renamed pipe from "+oldId+" to "+newId);
    			pipeStore.deletePipe(pipeid);
    		}
		}
    }
    public class CheckPassListener implements EventListener{
    	String pipeid;
    	public void setPipeId(String pipeid){
    		this.pipeid=pipeid;
    	}
    	public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
    		PipeConfig config = engine.getPipeStore().getPipe(pipeid);
    		if(config != null && config.isPasswordCorrect(checkPassText.getValue())){
    			engine.getPipeStore().deletePipe(pipeid);
    		   checkPassWin.setVisible(false);
    		}
    		else{
    			try{
    			    Messagebox.show("Password is incorrect, please re-enter the password for overwriting the PipeConfig!");
    			}
    			catch(java.lang.InterruptedException e){
    			}
    		}
    	}
    }
	/**
	 * @return
	 */
	public List getBrokenPipes() {
		PipeStore pipeStore = engine.getPipeStore();
		List brokenPipes = new ArrayList();
		for(PipeConfig config : pipeStore.getPipeList()){
			if(config.getId().startsWith("broken/")){
				brokenPipes.add(config);
			}
		}			
		return brokenPipes;
	}
	/**
	 * @return
	 */
	public List getOkPipes(String queryString) {
		PipeStore pipeStore = engine.getPipeStore();
		List okPipes = new ArrayList();
		
		String keywords[] = {}; 
		if (queryString!=null){
			keywords = queryString.split(" ");
			for (int i=0; i<keywords.length; i++){
				keywords[i] = keywords[i].toLowerCase();
			}
		}
		
		
		for(PipeConfig config : pipeStore.getPipeList()){
			if(!config.getId().startsWith("broken/")){
				
				boolean qualifies = true;
				/*
				 * only add it if the PipeConfig respects all keywords in the queryString
				 */
				for (int i=0; i<keywords.length; i++){
					boolean inId = config.getId().toLowerCase().contains(keywords[i]);
					boolean inDesc = config.getName().toLowerCase().contains(keywords[i]);
					
					if (!inId && !inDesc){
						qualifies = false;
						break;
					}
				}
				
				if (qualifies)
					okPipes.add(config);
			}
		}			
		return okPipes;
	}
	
	
	
	public void refreshPipeLists(String queryString) {

		Grid grid = (Grid)wsp.getFellow(fixedPipeGridId);
		grid.setModel(new SimpleListModel(getOkPipes(queryString)));
		grid = (Grid)wsp.getFellow(brokenPipeGridId);
		grid.setModel(new SimpleListModel(getBrokenPipes()));
		
		/**
		 * Also reload the preloaded Workflow Functions used by the GearsFunctionChooserNode
		 * 
		 * ignore the queryString
		 */
		FunctionLoader.reset();
	}
	
  }